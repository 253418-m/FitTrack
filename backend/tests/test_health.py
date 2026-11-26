from fastapi.testclient import TestClient

from app.main import app


def test_get_health_returns_process_status() -> None:
    client = TestClient(app)
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok"}
    assert response.headers["content-type"].startswith("application/json")


def test_health_does_not_accept_post() -> None:
    client = TestClient(app)
    response = client.post("/health")

    assert response.status_code == 405
