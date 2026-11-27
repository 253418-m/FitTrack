"""Pruebas reales solo sobre fittrack_test local; nunca usan SQLite ni fittrack_dev."""
from concurrent.futures import ThreadPoolExecutor
from uuid import UUID, uuid4

import pytest
from alembic import command
from alembic.config import Config
from fastapi.testclient import TestClient
from sqlalchemy import create_engine, inspect, text
from sqlalchemy.exc import IntegrityError
from sqlalchemy.engine import make_url
from sqlalchemy.orm import Session, sessionmaker

from app.auth.dependencies import require_roles
from app.core.config import Settings, get_settings
from app.core.errors import ApiError
from app.db.session import get_db
from app.main import app
from app.users.model import User, UserRole

PASSWORD = "contraseña-ficticia-larga"


@pytest.fixture(scope="module")
def database():
    try:
        settings = Settings()
    except Exception:
        pytest.skip("Faltan variables locales de PostgreSQL/JWT para integración")
    if settings.test_database_url is None:
        pytest.skip("TEST_DATABASE_URL no configurada")
    development = make_url(settings.database_url.get_secret_value())
    testing = make_url(settings.test_database_url.get_secret_value())
    assert testing.drivername == "postgresql+psycopg"
    assert testing.host in {"127.0.0.1", "localhost", "::1"}
    assert testing.database == "fittrack_test"
    assert development.database != testing.database
    assert settings.app_env in {"development", "test"}
    engine = create_engine(testing, pool_pre_ping=True, hide_parameters=True)
    with engine.connect() as connection:
        assert connection.scalar(text("select current_database()")) == "fittrack_test"
        current_role = connection.scalar(text("select current_user"))
        assert current_role.startswith("fittrack_")
        assert not connection.scalar(
            text("select rolsuper from pg_roles where rolname = current_user")
        )
        assert connection.scalar(
            text("select pg_get_userbyid(datdba) from pg_database where datname = current_database()")
        ) == current_role
    fresh_database = not inspect(engine).get_table_names()
    config = Config("alembic.ini")
    config.attributes["database_url"] = settings.test_database_url.get_secret_value()
    command.upgrade(config, "head")
    if fresh_database:
        # Solo la tabla creada por esta fixture puede retirarse aquí.
        command.downgrade(config, "base")
        command.upgrade(config, "head")
    yield settings, engine, fresh_database
    engine.dispose()


def test_migration_roundtrip_on_fresh_isolated_database(database):
    _, engine, fresh_database = database
    if not fresh_database:
        pytest.skip("La base de pruebas ya contenía tablas; no se ejecuta downgrade destructivo")
    with engine.connect() as connection:
        assert connection.scalar(text("select version_num from alembic_version")) == "bd7cce2c8602"


@pytest.fixture
def api(database):
    settings, engine, _ = database
    ids: list[UUID] = []
    factory = sessionmaker(bind=engine, expire_on_commit=False)

    def test_db():
        with factory() as session:
            yield session

    app.dependency_overrides[get_db] = test_db
    app.dependency_overrides[get_settings] = lambda: settings
    try:
        with TestClient(app) as client:
            yield client, ids, engine
    finally:
        app.dependency_overrides.clear()
        with factory() as session:
            for user_id in ids:
                user = session.get(User, user_id)
                if user is not None:
                    session.delete(user)
            session.commit()


def payload(username: str, email: str, role: str = "ATHLETE", name: str = "Nombre de prueba"):
    return {
        "name": name,
        "username": username,
        "email": email,
        "password": PASSWORD,
        "password_confirmation": PASSWORD,
        "role": role,
    }


def register(client: TestClient, ids: list[UUID], data: dict) -> dict:
    response = client.post("/api/v1/auth/register", json=data)
    assert response.status_code == 201, response.json()
    body = response.json()
    ids.append(UUID(body["user"]["id"]))
    assert body["token_type"] == "bearer"
    assert body["expires_in"] == 1800
    assert "password_hash" not in response.text
    return body


def test_register_both_roles_and_duplicate_personal_name(api):
    client, ids, engine = api
    suffix = uuid4().hex[:10]
    athlete = register(client, ids, payload("athlete_" + suffix, "a" + suffix + "@example.com"))
    trainer = register(
        client, ids, payload("trainer_" + suffix, "t" + suffix + "@example.com", "TRAINER")
    )
    assert athlete["user"]["name"] == trainer["user"]["name"]
    assert athlete["user"]["role"] == "ATHLETE"
    assert trainer["user"]["role"] == "TRAINER"
    with Session(engine) as session:
        user = session.get(User, UUID(athlete["user"]["id"]))
        assert user is not None
        assert user.password_hash.startswith("$argon2id$")
        assert user.password_hash != PASSWORD
        assert user.created_at.tzinfo is not None


def test_duplicate_username_and_email_are_case_insensitive(api):
    client, ids, _ = api
    suffix = uuid4().hex[:10]
    register(client, ids, payload("name_" + suffix, "mail" + suffix + "@example.com"))
    username_conflict = client.post(
        "/api/v1/auth/register",
        json=payload(("NAME_" + suffix).upper(), "other" + suffix + "@example.com"),
    )
    email_conflict = client.post(
        "/api/v1/auth/register",
        json=payload("other_" + suffix, ("MAIL" + suffix + "@EXAMPLE.COM").upper()),
    )
    assert username_conflict.status_code == 409
    assert username_conflict.json()["error"]["code"] == "USERNAME_CONFLICT"
    assert email_conflict.status_code == 409
    assert email_conflict.json()["error"]["code"] == "EMAIL_CONFLICT"
    # Una transacción fallida no contamina la siguiente solicitud.
    register(client, ids, payload("next_" + suffix, "next" + suffix + "@example.com"))


def test_login_and_me_use_persisted_identity(api):
    client, ids, engine = api
    suffix = uuid4().hex[:10]
    user = register(client, ids, payload("trainer_" + suffix, "t" + suffix + "@example.com", "TRAINER"))
    login = client.post(
        "/api/v1/auth/login",
        json={"email": ("T" + suffix + "@EXAMPLE.COM").upper(), "password": PASSWORD},
    )
    assert login.status_code == 200
    assert login.json()["user"]["id"] == user["user"]["id"]
    assert "password_hash" not in login.text
    me = client.get("/api/v1/users/me", headers={"Authorization": "Bearer " + login.json()["access_token"]})
    assert me.status_code == 200
    assert me.json()["role"] == "TRAINER"
    assert me.json()["id"] == user["user"]["id"]
    with Session(engine) as session:
        record = session.get(User, UUID(user["user"]["id"]))
        record.role = "ATHLETE"
        session.commit()
    updated = client.get("/api/v1/users/me", headers={"Authorization": "Bearer " + login.json()["access_token"]})
    assert updated.json()["role"] == "ATHLETE"


def test_invalid_credentials_and_token_are_generic(api):
    client, ids, _ = api
    suffix = uuid4().hex[:10]
    register(client, ids, payload("athlete_" + suffix, "a" + suffix + "@example.com"))
    bad_password = client.post(
        "/api/v1/auth/login",
        json={"email": "a" + suffix + "@example.com", "password": "wrong"},
    )
    missing_user = client.post(
        "/api/v1/auth/login",
        json={"email": "missing" + suffix + "@example.com", "password": "wrong"},
    )
    assert bad_password.status_code == missing_user.status_code == 401
    assert bad_password.json() == missing_user.json()
    assert bad_password.headers["www-authenticate"] == "Bearer"
    missing_token = client.get("/api/v1/users/me")
    altered = client.get("/api/v1/users/me", headers={"Authorization": "Bearer altered"})
    assert missing_token.status_code == altered.status_code == 401
    assert altered.headers["www-authenticate"] == "Bearer"


def test_database_rejects_invalid_role(api):
    _, _, engine = api
    suffix = uuid4().hex[:10]
    with Session(engine) as session:
        session.add(
            User(
                name="Rol inválido",
                username="bad_" + suffix,
                email="bad" + suffix + "@example.com",
                password_hash="test-only",
                role="ADMIN",
            )
        )
        with pytest.raises(IntegrityError):
            session.commit()
        session.rollback()


def test_migration_is_at_head(api):
    _, _, engine = api
    with engine.connect() as connection:
        assert connection.scalar(text("select version_num from alembic_version")) == "bd7cce2c8602"


def test_role_dependency_rejects_other_role(api):
    _, ids, engine = api
    suffix = uuid4().hex[:10]
    with Session(engine) as session:
        user = User(
            name="Prueba", username="role_" + suffix,
            email="role" + suffix + "@example.com",
            password_hash="test-only", role="TRAINER",
        )
        session.add(user)
        session.commit()
        ids.append(user.id)
        dependency = require_roles(UserRole.ATHLETE)
        with pytest.raises(ApiError) as error:
            dependency(user)
        assert error.value.status_code == 403
        assert require_roles(UserRole.TRAINER)(user) is user


@pytest.mark.parametrize("duplicate_field", ["username", "email"])
def test_concurrent_duplicate_registration_allows_one(api, duplicate_field):
    _, ids, engine = api
    suffix = uuid4().hex[:10]
    data = payload("race_" + suffix, "race" + suffix + "@example.com")
    second = data.copy()
    if duplicate_field == "username":
        second["email"] = "race_other" + suffix + "@example.com"
    else:
        second["username"] = "race_other_" + suffix

    def submit(item):
        with TestClient(app) as client:
            response = client.post("/api/v1/auth/register", json=item)
            return response.status_code, response.json()

    with ThreadPoolExecutor(max_workers=2) as pool:
        results = list(pool.map(submit, [data, second]))
    assert sorted(status for status, _ in results) == [201, 409]
    ids.extend(UUID(body["user"]["id"]) for status, body in results if status == 201)
    conflict_code = "USERNAME_CONFLICT" if duplicate_field == "username" else "EMAIL_CONFLICT"
    assert next(body for status, body in results if status == 409)["error"]["code"] == conflict_code
