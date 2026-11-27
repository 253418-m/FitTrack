from fastapi import APIRouter, Depends

from app.auth.dependencies import get_current_user
from app.users.model import User
from app.users.schemas import PublicUser

router = APIRouter(prefix="/api/v1/users", tags=["users"])


@router.get("/me", response_model=PublicUser)
def read_current_user(user: User = Depends(get_current_user)) -> User:
    return user
