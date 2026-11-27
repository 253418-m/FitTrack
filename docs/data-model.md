# Modelo relacional de FitTrack

**Estado:** la etapa 03 añadió el modelo SQLAlchemy y la migración Alembic `bd7cce2c8602` únicamente para `users`. Se aplicó y comprobó en PostgreSQL 18.6 local sobre `fittrack_dev` y `fittrack_test`, con roles propietarios separados y sin acceso cruzado. Las restantes entidades siguen planificadas. Desarrollo y pruebas usarán PostgreSQL local/aislado; producción usará PostgreSQL dentro de la misma EC2 que FastAPI, nunca RDS.

`users`: UUID generado por servidor; `username` y `email` únicos en base y normalizados por API; `name` no único; `password_hash` Argon2id; `role` limitado por CHECK a `ATHLETE` o `TRAINER`; `created_at` y `updated_at` son `TIMESTAMPTZ` UTC asignados por servidor. No se ejecuta `create_all` ni migración al importar la aplicación.

| Entidad | Clave y campos propuestos | Relaciones y restricciones |
|---|---|---|
| `users` | `id UUID PK`, `username`, `name`, `email`, `password_hash`, `role`, `created_at`, `updated_at` | Username y correo únicos normalizados; rol `ATHLETE/TRAINER`; hash nunca en API. |
| `trainer_requests` | `id UUID PK`, `athlete_id FK`, `trainer_id FK`, `status`, `created_at`, `resolved_at?` | Índices por participantes/estado; transición terminal. Índice parcial para una pendiente por atleta si se aprueba. |
| `trainer_athletes` | `id UUID PK`, `trainer_id FK`, `athlete_id FK`, `started_at`, `ended_at?`, `ended_by? FK` | Un entrenador tiene muchos vínculos; índice único parcial por atleta donde `ended_at IS NULL`. Historial conservado. |
| `routines` | `id UUID PK`, `trainer_athlete_id FK`, `name`, `muscle_group`, `level`, `duration_min`, `created_at`, `updated_at`, `deleted_at?` | Propietario y atleta se derivan del vínculo; duración positiva; exactamente un vínculo. |
| `exercises` | `id UUID PK`, `routine_id FK`, `name`, `sets`, `repetitions`, `position` | Series/repeticiones positivas, posición no negativa y única por rutina; al menos uno por rutina validado en transacción. |

La API validará roles, propiedad, vínculo activo y fechas. Aceptación de solicitud/vínculo, desvinculación, asignación y CRUD de rutina serán transaccionales; restricciones de PostgreSQL resolverán carreras de aceptación. No duplicar contadores ni IDs de dueño en rutinas. Reasignar dentro de rutinas del mismo entrenador; terminar vínculo revocará acceso del atleta según política propuesta. La eliminación lógica requiere aprobación de la estrategia Deshacer.

Datos Android: DataStore de sesión con ID, rol y token cifrado por Keystore; DataStore de hábitos con ID de usuario, fecha local e IDs de hábitos completados. La calculadora permanece en memoria. No almacenar contraseñas, rutinas remotas ni métricas del atleta en la cuenta entrenador.

## Extensión v5.0 propuesta, sin migración actual

| Entidad | Campos propuestos | Regla |
|---|---|---|
| device_registrations | id, user_id, installation_id, registration_token, is_active, created_at, updated_at | Instalación ligada a usuario autenticado; evitar registros activos duplicados; desvincular al logout. |
| notification_outbox | id, recipient_id, event_type, resource_id, status, attempts, created_at, next_attempt_at, processed_at | Registrar en transacción de negocio; procesar después de commit; reintentos controlados. |

Total previsto: siete entidades principales en PostgreSQL. Hábitos, pasos observados y preferencias por usuario permanecen en DataStore; no almacenar biometría. Estas dos entidades son propuestas técnicas, no tablas existentes.
