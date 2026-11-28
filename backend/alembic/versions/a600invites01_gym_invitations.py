"""Gym invitations and active accounts, preserving all existing users."""
from alembic import op
import sqlalchemy as sa

revision = "a600invites01"
down_revision = "bd7cce2c8602"
branch_labels = None
depends_on = None


def upgrade():
    op.add_column("users", sa.Column("is_active", sa.Boolean(), nullable=False, server_default=sa.true()))
    op.create_table("gym_invitations",
        sa.Column("id", sa.Uuid(), primary_key=True),
        sa.Column("code_hash", sa.String(64), nullable=False, unique=True),
        sa.Column("role", sa.String(7), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("expires_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("revoked_at", sa.DateTime(timezone=True)),
        sa.Column("consumed_at", sa.DateTime(timezone=True)),
        sa.Column("consumed_by", sa.Uuid(), sa.ForeignKey("users.id", ondelete="RESTRICT")),
        sa.CheckConstraint("role IN ('ATHLETE', 'TRAINER')", name="ck_invitation_role"),
        sa.CheckConstraint("expires_at > created_at", name="ck_invitation_expiry"),
        sa.CheckConstraint("(consumed_at IS NULL) = (consumed_by IS NULL)", name="ck_invitation_consumption"),
        sa.CheckConstraint("NOT (revoked_at IS NOT NULL AND consumed_at IS NOT NULL)", name="ck_invitation_terminal"))
    op.create_index("ix_gym_invitations_role", "gym_invitations", ["role"])


def downgrade():
    op.drop_table("gym_invitations")
    op.drop_column("users", "is_active")
