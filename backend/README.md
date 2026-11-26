# Backend local de FitTrack

Etapa 01: FastAPI expone solo `GET /health`. Su respuesta `{"status":"ok"}` comprueba el proceso, no PostgreSQL. El resto de la API es un contrato preliminar documentado en [docs/api-contract.md](../docs/api-contract.md).

## Requisitos

Python 3.11 o posterior. Usar un entorno virtual local, sin instalación global.

## Preparación en Windows PowerShell

Desde `backend/`:

```powershell
py -3.13 -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.lock
.\.venv\Scripts\python.exe -m pip install -e ".[test]" --no-deps
```

Se verificó Python 3.13.14 en este equipo. Si se usa otro intérprete, ajustar el comando y volver a resolver y validar `requirements.lock`. `pyproject.toml` fija dependencias directas y el lock registra la resolución transitiva observada para esta etapa.

## Ejecución y comprobación

```powershell
.\.venv\Scripts\python.exe -m uvicorn app.main:app --host 127.0.0.1 --port 8000
```

En otra terminal: `Invoke-RestMethod http://127.0.0.1:8000/health`.

```powershell
.\.venv\Scripts\python.exe -m pytest -q
.\.venv\Scripts\python.exe -m pip check
.\.venv\Scripts\python.exe -m compileall app
```

PostgreSQL local, SQLAlchemy y Alembic corresponden a la etapa 03. El despliegue en una única instancia EC2 corresponde a la etapa 21. No hay variables secretas ni base de datos configurada en esta etapa.

## Contexto v5.0 planificado

FastAPI y PostgreSQL seguirán siendo la autoridad de autenticación y datos. En etapa 17 se proponen instalaciones FCM y outbox transaccional para cuatro eventos remotos, con Firebase Admin SDK solo en servidor. No existen aún modelos, endpoints ni credenciales de Firebase. El contrato preliminar está en docs/api-contract.md.
