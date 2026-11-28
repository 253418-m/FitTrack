from datetime import datetime
from uuid import UUID, uuid4

from sqlalchemy import CheckConstraint, DateTime, ForeignKey, String, Uuid
from sqlalchemy.orm import Mapped, mapped_column
from app.db.base import Base
from app.users.model import utc_now


class GymInvitation(Base):
    __tablename__ = "gym_invitations"
    __table_args__ = (
        CheckConstraint("role IN ('ATHLETE', 'TRAINER')", name="ck_invitation_role"),
        CheckConstraint("expires_at > created_at", name="ck_invitation_expiry"),
        CheckConstraint("(consumed_at IS NULL) = (consumed_by IS NULL)", name="ck_invitation_consumption"),
        CheckConstraint("NOT (revoked_at IS NOT NULL AND consumed_at IS NOT NULL)", name="ck_invitation_terminal"),
    )
    id: Mapped[UUID] = mapped_column(Uuid, primary_key=True, default=uuid4)
    code_hash: Mapped[str] = mapped_column(String(64), unique=True, nullable=False)
    role: Mapped[str] = mapped_column(String(7), nullable=False, index=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=utc_now, nullable=False)
    expires_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    revoked_at: Mapped[datetime | None] = mapped_column(DateTime(timezone=True))
    consumed_at: Mapped[datetime | None] = mapped_column(DateTime(timezone=True))
    consumed_by: Mapped[UUID | None] = mapped_column(Uuid, ForeignKey("users.id", ondelete="RESTRICT"))
