from datetime import datetime
from uuid import UUID

from pydantic import BaseModel, ConfigDict

from app.users.model import UserRole


class PublicUser(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: UUID
    name: str
    username: str
    email: str
    role: UserRole
    created_at: datetime
