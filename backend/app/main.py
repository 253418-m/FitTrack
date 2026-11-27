from fastapi import FastAPI

from app.auth.router import router as auth_router
from app.core.errors import install_error_handlers
from app.users.router import router as users_router

app = FastAPI(title="FitTrack API")
install_error_handlers(app)
app.include_router(auth_router)
app.include_router(users_router)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}
