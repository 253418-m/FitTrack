from functools import lru_cache
from pathlib import Path

from pydantic import Field, SecretStr, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=Path(__file__).resolve().parents[2] / ".env",
        extra="ignore",
    )

    gym_athlete_limit: int = Field(default=100, ge=1)
    gym_trainer_limit: int = Field(default=10, ge=1)
    invitation_expire_hours: int = Field(default=48, ge=1, le=8760)
    app_env: str = "development"
    database_url: SecretStr
    test_database_url: SecretStr | None = None
    jwt_secret_key: SecretStr
    jwt_access_token_expire_minutes: int = Field(default=30, ge=1, le=1440)

    @field_validator("jwt_secret_key")
    @classmethod
    def validate_secret(cls, value: SecretStr) -> SecretStr:
        secret = value.get_secret_value()
        if len(secret.encode("utf-8")) < 32 or "<" in secret or ">" in secret:
            raise ValueError("JWT_SECRET_KEY requiere al menos 32 bytes aleatorios sin marcadores")
        return value

    @field_validator("database_url")
    @classmethod
    def validate_database_url(cls, value: SecretStr) -> SecretStr:
        if not value.get_secret_value().startswith("postgresql+psycopg://"):
            raise ValueError("DATABASE_URL debe usar PostgreSQL con psycopg")
        return value


@lru_cache
def get_settings() -> Settings:
    return Settings()
