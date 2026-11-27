from sqlalchemy.exc import IntegrityError, SQLAlchemyError
from sqlalchemy.orm import Session

from app.auth.schemas import LoginRequest, RegisterRequest, SessionResponse
from app.core.config import Settings
from app.core.errors import ApiError
from app.core.security import (
    create_access_token,
    hash_password,
    verify_missing_user_password,
    verify_password,
)
from app.users.model import User
from app.users.repository import add_user, get_by_email


def session_response(user: User, settings: Settings) -> SessionResponse:
    token, seconds = create_access_token(user.id, settings)
    return SessionResponse(access_token=token, expires_in=seconds, user=user)


def register(db: Session, data: RegisterRequest, settings: Settings) -> SessionResponse:
    user = User(
        name=data.name,
        username=data.username,
        email=str(data.email),
        password_hash=hash_password(data.password),
        role=data.role.value,
    )
    add_user(db, user)
    try:
        db.commit()
    except IntegrityError as exc:
        db.rollback()
        constraint = getattr(getattr(exc.orig, "diag", None), "constraint_name", None)
        if constraint == "users_email_key":
            raise ApiError(409, "EMAIL_CONFLICT", "El correo ya está registrado.") from None
        if constraint == "users_username_key":
            raise ApiError(409, "USERNAME_CONFLICT", "El username ya está registrado.") from None
        raise ApiError(503, "SERVICE_UNAVAILABLE", "Servicio temporalmente no disponible.") from None
    except SQLAlchemyError:
        db.rollback()
        raise ApiError(503, "SERVICE_UNAVAILABLE", "Servicio temporalmente no disponible.") from None
    return session_response(user, settings)


def login(db: Session, data: LoginRequest, settings: Settings) -> SessionResponse:
    user = get_by_email(db, str(data.email))
    if user is None:
        verify_missing_user_password(data.password)
        raise ApiError(
            401,
            "INVALID_CREDENTIALS",
            "Correo o contraseña incorrectos.",
            {"WWW-Authenticate": "Bearer"},
        )
    if not verify_password(data.password, user.password_hash):
        raise ApiError(
            401,
            "INVALID_CREDENTIALS",
            "Correo o contraseña incorrectos.",
            {"WWW-Authenticate": "Bearer"},
        )
    return session_response(user, settings)
