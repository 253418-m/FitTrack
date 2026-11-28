"""Private operator CLI. No administrative HTTP endpoints."""
import argparse
from uuid import UUID
from sqlalchemy import create_engine, select
from sqlalchemy.orm import Session
from app.core.config import Settings
from app.core.errors import ApiError
from app.invitations.model import GymInvitation
from app.invitations.service import capacity, create_invitation, deactivate_user, lock_role, revoke_invitation
from app.users.model import User, utc_now


def main():
    parser = argparse.ArgumentParser(description="FitTrack Gym: operador privado")
    commands = parser.add_subparsers(dest="command", required=True)
    for name in ("capacity", "invite"):
        command = commands.add_parser(name)
        command.add_argument("--role", choices=["ATHLETE", "TRAINER"], required=True)
    commands.add_parser("list")
    commands.add_parser("legacy-users")
    for name in ("revoke", "deactivate"):
        command = commands.add_parser(name)
        command.add_argument("id", type=UUID)
    args = parser.parse_args()
    settings = Settings()
    engine = create_engine(settings.database_url.get_secret_value(), hide_parameters=True)
    try:
        with Session(engine, expire_on_commit=False) as db, db.begin():
            if args.command == "capacity":
                lock_role(db, args.role)
                print(capacity(db, args.role, settings))
            elif args.command == "invite":
                item, code = create_invitation(db, args.role, settings)
                # Commit before exposing the only plaintext copy to the authorized terminal.
                db.commit()
                print(f"Invitación {item.id}: {code}")
            elif args.command == "list":
                for item in db.scalars(select(GymInvitation).order_by(GymInvitation.created_at)):
                    state = "CONSUMED" if item.consumed_at else "REVOKED" if item.revoked_at else "EXPIRED" if item.expires_at <= utc_now() else "PENDING"
                    print(item.id, item.role, state, item.created_at, item.expires_at)
            elif args.command == "legacy-users":
                consumed = select(GymInvitation.consumed_by).where(GymInvitation.consumed_by.is_not(None))
                for user in db.scalars(select(User).where(User.id.not_in(consumed))):
                    print(user.id, user.role, "active" if user.is_active else "inactive")
            elif args.command == "revoke":
                revoke_invitation(db, args.id)
            elif args.command == "deactivate":
                deactivate_user(db, args.id)
    except (ApiError, ValueError) as error:
        parser.exit(1, f"{getattr(error, 'code', 'INVALID_OPERATION')}: operación no completada.\n")
    finally:
        engine.dispose()


if __name__ == "__main__":
    main()
