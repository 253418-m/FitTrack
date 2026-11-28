from collections.abc import Callable
from uuid import UUID

from fastapi import Depends
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jwt.exceptions import InvalidTokenError
from sqlalchemy.orm import Session

from app.core.config import Settings, get_settings
from app.core.errors import ApiError
from app.core.security import decode_access_token
from app.db.session import get_db
from app.users.model import User, UserRole
from app.users.repository import get_by_id

_bearer = HTTPBearer(auto_error=False)


def get_current_user(
    credentials: HTTPAuthorizationCredentials | None = Depends(_bearer),
    db: Session = Depends(get_db),
    settings: Settings = Depends(get_settings),
) -> User:
    if credentials is None:
        raise ApiError(401, "UNAUTHENTICATED", "Autenticación requerida.", {"WWW-Authenticate": "Bearer"})
    try:
        user_id: UUID = decode_access_token(credentials.credentials, settings)
    except (InvalidTokenError, ValueError, TypeError, KeyError):
        raise ApiError(401, "UNAUTHENTICATED", "Token inválido o expirado.", {"WWW-Authenticate": "Bearer"}) from None
    user = get_by_id(db, user_id)
    if user is None or not user.is_active:
        raise ApiError(401, "UNAUTHENTICATED", "Token inválido o expirado.", {"WWW-Authenticate": "Bearer"})
    return user


def require_roles(*allowed: UserRole) -> Callable[..., User]:
    def dependency(user: User = Depends(get_current_user)) -> User:
        if user.role not in allowed:
            raise ApiError(403, "FORBIDDEN", "Acceso no permitido.")
        return user

    return dependency
