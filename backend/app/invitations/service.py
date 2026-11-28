import hashlib
import secrets
from datetime import timedelta
from uuid import UUID

from sqlalchemy import func, select, text
from sqlalchemy.orm import Session
from app.core.config import Settings
from app.core.errors import ApiError
from app.invitations.model import GymInvitation
from app.users.model import User, UserRole


def code_hash(code: str) -> str:
    return hashlib.sha256(code.encode("utf-8")).hexdigest()


def lock_role(db: Session, role: str) -> None:
    # Stable per-role transaction lock shared by issuance, consumption, revoke and deactivation.
    key = {"ATHLETE": 60001, "TRAINER": 60002}[role]
    db.execute(text("SELECT pg_advisory_xact_lock(:key)"), {"key": key})


def capacity(db: Session, role: str, settings: Settings) -> dict:
    now = db.scalar(select(func.clock_timestamp()))
    active = db.scalar(select(func.count()).select_from(User).where(User.role == role, User.is_active.is_(True)))
    pending = db.scalar(select(func.count()).select_from(GymInvitation).where(
        GymInvitation.role == role, GymInvitation.expires_at > now,
        GymInvitation.revoked_at.is_(None), GymInvitation.consumed_at.is_(None)))
    limit = settings.gym_athlete_limit if role == "ATHLETE" else settings.gym_trainer_limit
    return {"role": role, "limit": limit, "active": active, "reserved": pending, "available": max(0, limit-active-pending)}


def create_invitation(db: Session, role: str, settings: Settings) -> tuple[GymInvitation, str]:
    role = UserRole(role).value
    lock_role(db, role)
    if capacity(db, role, settings)["available"] == 0:
        raise ApiError(409, "GYM_CAPACITY_REACHED", "No hay cupo disponible.")
    code = secrets.token_urlsafe(32)
    now = db.scalar(select(func.clock_timestamp()))
    invitation = GymInvitation(code_hash=code_hash(code), role=role, created_at=now,
                               expires_at=now + timedelta(hours=settings.invitation_expire_hours))
    db.add(invitation)
    db.flush()
    return invitation, code


def invitation_for_registration(db: Session, code: str, settings: Settings) -> GymInvitation:
    invitation = db.scalar(select(GymInvitation).where(GymInvitation.code_hash == code_hash(code)))
    if invitation is None:
        raise ApiError(400, "INVALID_INVITATION", "Invitación no válida.")
    lock_role(db, invitation.role)
    invitation = db.scalar(select(GymInvitation).where(GymInvitation.id == invitation.id)
                           .with_for_update().execution_options(populate_existing=True))
    now = db.scalar(select(func.clock_timestamp()))
    if invitation.consumed_at or invitation.revoked_at or invitation.expires_at <= now:
        raise ApiError(400, "INVALID_INVITATION", "Invitación no válida.")
    status = capacity(db, invitation.role, settings)
    if status["active"] >= status["limit"] or status["active"] + status["reserved"] > status["limit"]:
        raise ApiError(409, "GYM_CAPACITY_REACHED", "No hay cupo disponible.")
    return invitation


def revoke_invitation(db: Session, invitation_id: UUID) -> None:
    item = db.get(GymInvitation, invitation_id)
    if item is None:
        raise ValueError("Invitación inexistente")
    lock_role(db, item.role)
    db.refresh(item, with_for_update=True)
    if item.consumed_at is not None:
        raise ValueError("No se puede revocar una invitación consumida")
    if item.revoked_at is None:
        item.revoked_at = db.scalar(select(func.clock_timestamp()))


def deactivate_user(db: Session, user_id: UUID) -> None:
    user = db.get(User, user_id)
    if user is None:
        raise ValueError("Usuario inexistente")
    lock_role(db, user.role)
    db.refresh(user, with_for_update=True)
    user.is_active = False
