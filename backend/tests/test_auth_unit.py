from datetime import datetime, timedelta, timezone
from types import SimpleNamespace
from uuid import uuid4

import jwt
import pytest
from fastapi.testclient import TestClient
from pydantic import ValidationError
from sqlalchemy.exc import IntegrityError

from app.auth.dependencies import require_roles
from app.auth.schemas import LoginRequest, RegisterRequest
from app.auth.service import register
from app.core.config import Settings, get_settings
from app.core.errors import ApiError
from app.core.security import (
    ALGORITHM,
    AUDIENCE,
    ISSUER,
    create_access_token,
    decode_access_token,
    hash_password,
    password_needs_rehash,
    verify_password,
)
from app.db.session import get_db
from app.main import app
from app.users.model import User, UserRole

TEST_SECRET = "unit-test-only-secret-with-at-least-32-bytes"
TEST_URL = "postgresql+psycopg://unit:test@127.0.0.1/fittrack_test"


def test_password_hash_uses_argon2id_and_random_salt() -> None:
    first = hash_password("Una contraseña larga de prueba")
    second = hash_password("Una contraseña larga de prueba")
    assert first.startswith("$argon2id$")
    assert first != second
    assert verify_password("Una contraseña larga de prueba", first)
    assert not verify_password("incorrecta", first)
    assert not password_needs_rehash(first)


def test_registration_normalizes_identifiers_without_changing_password() -> None:
    data = RegisterRequest(
        name="  María López  ",
        username="  Atleta.Demo_1  ",
        email="  ATLETA@EXAMPLE.COM ",
        password="  contraseña válida  ",
        password_confirmation="  contraseña válida  ",
        invitation_code="unit-test-invitation-code",
    )
    assert data.name == "María López"
    assert data.username == "atleta.demo_1"
    assert str(data.email) == "atleta@example.com"
    assert data.password == "  contraseña válida  "
    assert data.invitation_code == "unit-test-invitation-code"


@pytest.mark.parametrize(
    "changes",
    [
        {"name": "  "},
        {"username": "bad-name"},
        {"username": "ab"},
        {"email": "not-an-email"},
        {"password": "short", "password_confirmation": "short"},
        {"password_confirmation": "different-password"},
        {"role": "ADMIN"},
        {"id": str(uuid4())},
        {"password_hash": "injected"},
    ],
)
def test_registration_rejects_invalid_or_private_fields(changes: dict[str, str]) -> None:
    payload = {
        "name": "Usuario de prueba",
        "username": "atleta_demo",
        "email": "atleta@example.com",
        "password": "contraseña-de-prueba",
        "password_confirmation": "contraseña-de-prueba",
        "invitation_code": "unit-test-invitation-code",
    }
    payload.update(changes)
    with pytest.raises(ValidationError):
        RegisterRequest.model_validate(payload)


def _settings() -> Settings:
    return Settings(database_url=TEST_URL, jwt_secret_key=TEST_SECRET)


@pytest.mark.parametrize(
    "invalid_secret",
    ["too-short", "<generate_a_random_secret_of_at_least_32_bytes>"],
)
def test_settings_rejects_short_or_example_jwt_key(invalid_secret: str) -> None:
    with pytest.raises(ValidationError):
        Settings(database_url=TEST_URL, jwt_secret_key=invalid_secret)


def test_jwt_has_required_claims_and_configured_expiration() -> None:
    settings = Settings(
        database_url=TEST_URL,
        jwt_secret_key=TEST_SECRET,
        jwt_access_token_expire_minutes=45,
    )
    user_id = uuid4()
    token, seconds = create_access_token(user_id, settings)
    claims = jwt.decode(
        token,
        TEST_SECRET,
        algorithms=[ALGORITHM],
        issuer=ISSUER,
        audience=AUDIENCE,
    )
    assert seconds == 2700
    assert claims["sub"] == str(user_id)
    assert set(claims) == {"sub", "iat", "exp", "iss", "aud"}
    assert decode_access_token(token, settings) == user_id


@pytest.mark.parametrize(
    "changes",
    [
        {"exp": datetime.now(timezone.utc) - timedelta(minutes=1)},
        {"iss": "other-api"},
        {"aud": "other-client"},
        {"sub": "not-a-uuid"},
        {"sub": None},
        {"iat": None},
    ],
)
def test_jwt_rejects_invalid_claims(changes: dict[str, object]) -> None:
    now = datetime.now(timezone.utc)
    claims = {
        "sub": str(uuid4()),
        "iat": now,
        "exp": now + timedelta(minutes=30),
        "iss": ISSUER,
        "aud": AUDIENCE,
    }
    claims.update(changes)
    token = jwt.encode(claims, TEST_SECRET, algorithm=ALGORITHM)
    with pytest.raises((jwt.InvalidTokenError, ValueError, TypeError)):
        decode_access_token(token, _settings())


def test_jwt_rejects_modified_and_unsigned_tokens() -> None:
    token, _ = create_access_token(uuid4(), _settings())
    with pytest.raises(jwt.InvalidTokenError):
        decode_access_token(token + "x", _settings())
    unsigned = jwt.encode(
        {"sub": str(uuid4()), "iat": 1, "exp": 9999999999, "iss": ISSUER, "aud": AUDIENCE},
        key="",
        algorithm="none",
    )
    with pytest.raises(jwt.InvalidTokenError):
        decode_access_token(unsigned, _settings())


def test_validation_errors_do_not_echo_password_or_token() -> None:
    def fake_db():
        yield object()

    app.dependency_overrides[get_db] = fake_db
    app.dependency_overrides[get_settings] = _settings
    try:
        with TestClient(app) as client:
            response = client.post(
                "/api/v1/auth/register",
                json={
                    "name": "Prueba",
                    "username": "test_user",
                    "email": "test@example.com",
                    "password": "SENSITIVE_PASSWORD_VALUE",
                    "password_confirmation": "not matching",
                    "role": "ATHLETE",
                    "access_token": "SENSITIVE_TOKEN_VALUE",
                },
            )
        assert response.status_code == 422
        assert response.json()["error"]["code"] == "VALIDATION_ERROR"
        assert "SENSITIVE_PASSWORD_VALUE" not in response.text
        assert "SENSITIVE_TOKEN_VALUE" not in response.text
    finally:
        app.dependency_overrides.clear()


def test_missing_bearer_token_returns_uniform_401() -> None:
    def fake_db():
        yield object()

    app.dependency_overrides[get_db] = fake_db
    app.dependency_overrides[get_settings] = _settings
    try:
        with TestClient(app) as client:
            response = client.get("/api/v1/users/me")
        assert response.status_code == 401
        assert response.headers["www-authenticate"] == "Bearer"
        assert response.json()["error"]["code"] == "UNAUTHENTICATED"
    finally:
        app.dependency_overrides.clear()


def test_openapi_has_only_current_business_routes() -> None:
    paths = app.openapi()["paths"]
    assert set(paths) == {
        "/health",
        "/api/v1/auth/register",
        "/api/v1/auth/login",
        "/api/v1/users/me",
    }


@pytest.mark.parametrize(
    ("constraint", "code"),
    [
        ("users_username_key", "USERNAME_CONFLICT"),
        ("users_email_key", "EMAIL_CONFLICT"),
    ],
)
def test_registration_rolls_back_after_unique_conflict(constraint: str, code: str, monkeypatch) -> None:
    class FailingSession:
        rolled_back = False

        def add(self, user: User) -> None:
            self.user = user

        def flush(self) -> None:
            pass

        def commit(self) -> None:
            original = SimpleNamespace(diag=SimpleNamespace(constraint_name=constraint))
            raise IntegrityError("insert", {}, original)

        def rollback(self) -> None:
            self.rolled_back = True

    monkeypatch.setattr("app.auth.service.invitation_for_registration", lambda *args: SimpleNamespace(role="ATHLETE"))
    session = FailingSession()
    data = RegisterRequest(
        name="Prueba", username="unique_user", email="unique@example.com",
        password="contraseña-larga", password_confirmation="contraseña-larga",
        invitation_code="unit-test-invitation-code",
    )
    with pytest.raises(ApiError) as error:
        register(session, data, _settings())
    assert error.value.status_code == 409
    assert error.value.code == code
    assert session.rolled_back


def test_role_dependency_uses_persisted_user_role() -> None:
    user = User(
        id=uuid4(), name="Prueba", username="athlete",
        email="athlete@example.com", password_hash="test-only", role="ATHLETE",
        created_at=datetime.now(timezone.utc), updated_at=datetime.now(timezone.utc),
    )
    assert require_roles(UserRole.ATHLETE)(user) is user
    with pytest.raises(ApiError) as error:
        require_roles(UserRole.TRAINER)(user)
    assert error.value.status_code == 403
    assert error.value.code == "FORBIDDEN"


@pytest.mark.parametrize("missing", ["sub", "iat", "exp", "iss", "aud"])
def test_jwt_rejects_missing_required_claim(missing: str) -> None:
    now = datetime.now(timezone.utc)
    claims = {
        "sub": str(uuid4()),
        "iat": now,
        "exp": now + timedelta(minutes=30),
        "iss": ISSUER,
        "aud": AUDIENCE,
    }
    del claims[missing]
    token = jwt.encode(claims, TEST_SECRET, algorithm=ALGORITHM)
    with pytest.raises(jwt.InvalidTokenError):
        decode_access_token(token, _settings())


def test_jwt_rejects_other_signed_algorithm() -> None:
    now = datetime.now(timezone.utc)
    long_test_key = TEST_SECRET + "extra-test-only-key-material"
    token = jwt.encode(
        {
            "sub": str(uuid4()), "iat": now, "exp": now + timedelta(minutes=30),
            "iss": ISSUER, "aud": AUDIENCE,
        },
        long_test_key,
        algorithm="HS384",
    )
    settings = Settings(database_url=TEST_URL, jwt_secret_key=long_test_key)
    with pytest.raises(jwt.InvalidTokenError):
        decode_access_token(token, settings)


def test_me_rejects_signed_token_for_nonexistent_user() -> None:
    class EmptySession:
        def get(self, model: type, user_id: object) -> None:
            return None

    def fake_db():
        yield EmptySession()

    app.dependency_overrides[get_db] = fake_db
    app.dependency_overrides[get_settings] = _settings
    try:
        token, _ = create_access_token(uuid4(), _settings())
        with TestClient(app) as client:
            response = client.get(
                "/api/v1/users/me", headers={"Authorization": "Bearer " + token}
            )
        assert response.status_code == 401
        assert response.json()["error"]["code"] == "UNAUTHENTICATED"
        assert response.headers["www-authenticate"] == "Bearer"
    finally:
        app.dependency_overrides.clear()


def test_login_password_limit_and_exact_value() -> None:
    value = " " + "x" * 126 + " "
    data = LoginRequest(email="USER@EXAMPLE.COM", password=value)
    assert data.password == value
    assert str(data.email) == "user@example.com"
    with pytest.raises(ValidationError):
        LoginRequest(email="user@example.com", password="x" * 129)


def test_validation_error_redacts_unknown_field_names() -> None:
    def fake_db():
        yield object()

    app.dependency_overrides[get_db] = fake_db
    app.dependency_overrides[get_settings] = _settings
    try:
        with TestClient(app) as client:
            response = client.post(
                "/api/v1/auth/login",
                json={
                    "email": "user@example.com",
                    "password": "x" * 129,
                    "SENSITIVE_TOKEN_AS_FIELD_NAME": "SENSITIVE_VALUE",
                },
            )
        assert response.status_code == 422
        assert "SENSITIVE_TOKEN_AS_FIELD_NAME" not in response.text
        assert "SENSITIVE_VALUE" not in response.text
        assert "x" * 129 not in response.text
        assert response.json()["error"]["code"] == "VALIDATION_ERROR"
    finally:
        app.dependency_overrides.clear()
