from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.auth.schemas import LoginRequest, RegisterRequest, SessionResponse
from app.auth.service import login, register
from app.core.config import Settings, get_settings
from app.db.session import get_db

router = APIRouter(prefix="/api/v1/auth", tags=["auth"])


@router.post("/register", response_model=SessionResponse, status_code=201)
def register_user(
    data: RegisterRequest,
    db: Session = Depends(get_db),
    settings: Settings = Depends(get_settings),
) -> SessionResponse:
    return register(db, data, settings)


@router.post("/login", response_model=SessionResponse)
def login_user(
    data: LoginRequest,
    db: Session = Depends(get_db),
    settings: Settings = Depends(get_settings),
) -> SessionResponse:
    return login(db, data, settings)
