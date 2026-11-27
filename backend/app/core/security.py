from datetime import datetime, timedelta, timezone
from uuid import UUID

import jwt
from argon2 import PasswordHasher
from argon2.exceptions import InvalidHashError, VerifyMismatchError, VerificationError

from app.core.config import Settings

ISSUER = "fittrack-api"
AUDIENCE = "fittrack-clients"
ALGORITHM = "HS256"

_password_hasher = PasswordHasher(
    time_cost=2, memory_cost=19456, parallelism=1, hash_len=32, salt_len=16
)
_DUMMY_HASH = _password_hasher.hash("dummy-password-never-used-for-login")


def hash_password(password: str) -> str:
    return _password_hasher.hash(password)


def verify_password(password: str, password_hash: str) -> bool:
    try:
        return _password_hasher.verify(password_hash, password)
    except (InvalidHashError, VerifyMismatchError, VerificationError):
        return False


def password_needs_rehash(password_hash: str) -> bool:
    return _password_hasher.check_needs_rehash(password_hash)


def verify_missing_user_password(password: str) -> None:
    verify_password(password, _DUMMY_HASH)


def create_access_token(user_id: UUID, settings: Settings) -> tuple[str, int]:
    now = datetime.now(timezone.utc)
    seconds = settings.jwt_access_token_expire_minutes * 60
    token = jwt.encode(
        {
            "sub": str(user_id),
            "iat": now,
            "exp": now + timedelta(seconds=seconds),
            "iss": ISSUER,
            "aud": AUDIENCE,
        },
        settings.jwt_secret_key.get_secret_value(),
        algorithm=ALGORITHM,
    )
    return token, seconds


def decode_access_token(token: str, settings: Settings) -> UUID:
    claims = jwt.decode(
        token,
        settings.jwt_secret_key.get_secret_value(),
        algorithms=[ALGORITHM],
        issuer=ISSUER,
        audience=AUDIENCE,
        options={"require": ["sub", "iat", "exp", "iss", "aud"]},
    )
    return UUID(claims["sub"])
