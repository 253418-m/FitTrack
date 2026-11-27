# Backend local de FitTrack

**Estado de etapa 03:** backend implementado y validado localmente, pendiente de revisión y commit. `GET /health` sigue comprobando solo el proceso. Las rutas de autenticación y la migración se validaron sobre PostgreSQL 18.6.

## Requisitos e instalación

Python 3.11 o posterior; en esta revisión se utilizó 3.13.14. El servidor PostgreSQL 18.6 se verificó mediante conexión SQL (no solo por el cliente psql). Usar exclusivamente `backend/.venv`.

Desde `backend/` en PowerShell:

```powershell
py -3.13 -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.lock
.\.venv\Scripts\python.exe -m pip install -e ".[test]" --no-deps
.\.venv\Scripts\python.exe -m pip check
```

El lock registra la resolución observada en Windows/Python 3.13.14. `pyproject.toml` fija las dependencias directas. FastAPI y Uvicorn conservan sus versiones existentes. Las pruebas usan `httpx2`, recomendado por Starlette para `TestClient`.

## PostgreSQL local seguro

Crear dos bases **exclusivas**: `fittrack_dev` y `fittrack_test`. Crear roles separados, sin SUPERUSER, CREATEDB ni CREATEROLE, con permisos solo en su base. La administración debe realizarse con una cuenta local autorizada; no reutilizar credenciales de otros proyectos. No crear ni borrar bases existentes sin verificar su pertenencia. No abrir el puerto 5432 al público.

Ejemplo orientativo para un administrador PostgreSQL, **solo si los nombres no existen y las bases son nuevas** (sustituir secretos mediante un mecanismo local seguro, nunca en Git ni en el chat):

```sql
CREATE ROLE fittrack_dev_user LOGIN PASSWORD '<secreto-local-generado>';
CREATE ROLE fittrack_test_user LOGIN PASSWORD '<otro-secreto-local-generado>';
CREATE DATABASE fittrack_dev OWNER fittrack_dev_user;
CREATE DATABASE fittrack_test OWNER fittrack_test_user;
```

En esta validación `fittrack_dev_user` y `fittrack_test_user` fueron propietarios de sus bases respectivas, sin SUPERUSER, CREATEDB, CREATEROLE, REPLICATION ni BYPASSRLS. Cada rol pudo crear en su esquema y no tuvo CONNECT sobre la otra base. Las pruebas verifican motor PostgreSQL, host local, base `fittrack_test`, propietario y rol no superusuario antes de migrar o limpiar datos propios. El servidor escuchaba en `*` (0.0.0.0 y ::); no se pudo consultar el firewall sin privilegios de administrador ni `pg_hba_file_rules` con estos roles. No se comprobó exposición externa y no se modificó la configuración global.

Copiar `.env.example` a `.env` en `backend/` y configurar localmente `DATABASE_URL`, `TEST_DATABASE_URL` y `JWT_SECRET_KEY` con al menos 32 bytes aleatorios. `.env` está ignorado por Git. `APP_ENV` puede ser `development`; `JWT_ACCESS_TOKEN_EXPIRE_MINUTES` inicia en 30. No imprimir la configuración efectiva ni secretos. El esquema esperado es `postgresql+psycopg://...`. La aplicación falla de forma segura si falta configuración para autenticación; `/health` permanece disponible.

## Migraciones

Desde `backend/`, una vez verificada la base de desarrollo:

```powershell
.\.venv\Scripts\python.exe -m alembic heads
.\.venv\Scripts\python.exe -m alembic current
.\.venv\Scripts\python.exe -m alembic history
.\.venv\Scripts\python.exe -m alembic upgrade head
.\.venv\Scripts\python.exe -m alembic check
```

La revisión inicial `bd7cce2c8602` creó exclusivamente `users` en `fittrack_dev` y `fittrack_test`; `alembic current` quedó en head y `alembic check` no detectó operaciones nuevas. El primer roundtrip upgrade/downgrade/upgrade se ejecutó solo en `fittrack_test` inicialmente vacía. No se ejecutan migraciones al importar la aplicación; nunca hacer downgrade en desarrollo con usuarios.

## API y comprobación

```powershell
.\.venv\Scripts\python.exe -m uvicorn app.main:app --host 127.0.0.1 --port 8000
```

Rutas de negocio actuales: `GET /health`, `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `GET /api/v1/users/me`. Documentación automática: `/docs` y `/openapi.json`. Ver [contrato REST](../docs/api-contract.md).

```powershell
.\.venv\Scripts\python.exe -m pytest -q
.\.venv\Scripts\python.exe -m pip check
.\.venv\Scripts\python.exe -m compileall app
```

La primera ejecución sobre `fittrack_test` vacía aprobó 48 pruebas, incluido upgrade/downgrade/upgrade; la repetición aprobó 47 y omitió deliberadamente el segundo downgrade porque la base ya tenía tablas. Si falta `TEST_DATABASE_URL`, las pruebas de integración se omiten y un resultado verde **no valida PostgreSQL**. No usar SQLite. Se conservan las dos pruebas de `/health`.

## Seguridad

Contraseñas Argon2id (19 MiB, 2 iteraciones, paralelismo 1), JWT HS256 con `sub` UUID, `iat`, `exp`, `iss` y `aud`. Registro y login limitan la contraseña a 128 caracteres sin truncarla ni normalizarla; SQLAlchemy oculta parámetros en sus errores. `PublicUser` incluye email solo para usuario autenticado: los futuros perfiles públicos necesitan otro esquema sin email. La clave se obtiene del entorno, nunca del repositorio. Los JWT duran 30 minutos por defecto; no hay refresh ni revocación inmediata. El cliente debe volver a autenticarse al expirar. La API no expone hashes, tokens en URL ni trazas. Se necesita protección frente a intentos repetidos de login antes del despliegue. Las pruebas HTTP reales validaron 201, 200, 409, 422 y 401; dos usuarios ficticios `example.com` permanecen en `fittrack_dev` como datos de demostración. No se borraron usuarios existentes.

La evidencia técnica redactada se guardó localmente en el directorio hermano `FitTrack-references/evidence/stage-03/validation.txt`, fuera del repositorio. El despliegue en EC2 corresponde a la etapa 21. No hay recursos AWS/Firebase creados.
