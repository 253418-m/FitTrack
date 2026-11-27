# Contrato REST de FitTrack

**Estado:** `GET /health` → 200 `{"status":"ok"}` continúa midiendo solo el proceso FastAPI. En etapa 03 están implementados `POST /api/v1/auth/register`, `POST /api/v1/auth/login` y `GET /api/v1/users/me`; su validación PostgreSQL y HTTP local aprobó en bases exclusivas FitTrack. Las demás rutas de la tabla siguen planificadas bajo `/api/v1`.

## Contrato implementado en código

`POST /api/v1/auth/register` (201): JSON con `name`, `username`, `email`, `password`, `password_confirmation`, `role` (`ATHLETE` o `TRAINER`). `username` se recorta, convierte a minúsculas y admite 3–30 caracteres ASCII: letras, dígitos, punto y guion bajo. `name` se recorta, admite 1–100 caracteres y no es único. El correo se valida, recorta y normaliza a minúsculas, máximo 254 caracteres. La contraseña se conserva exactamente, de 8–128 caracteres; la confirmación debe coincidir. Los campos adicionales se rechazan. Duplicados de username o correo → 409. Datos inválidos → 422.

`POST /api/v1/auth/login` (200): JSON `{"email":"usuario@example.com","password":"<valor-local>"}`. Correo insensible a mayúsculas; contraseña de 1–128 caracteres, conservada sin normalizar ni truncar. Credenciales incorrectas → 401 con mensaje genérico.

Ambos éxitos devuelven `access_token: string`, `token_type: "bearer"`, `expires_in: integer` (segundos) y `user`: `id: UUID`, `name: string`, `username: string`, `email: string`, `role: "ATHLETE"|"TRAINER"`, `created_at: fecha ISO 8601 UTC`. Ningún campo es nullable. Nombres en snake_case. No se devuelve `password_hash`.

`GET /api/v1/users/me` (200) exige `Authorization: Bearer <JWT>`, resuelve el UUID firmado y vuelve a consultar el usuario en PostgreSQL; devuelve el mismo objeto `user` sin envoltura. Token ausente, inválido, expirado o usuario inexistente → 401 con `WWW-Authenticate: Bearer`. Una dependencia reutilizable de roles devuelve 403 para rol no permitido; no se creó un endpoint ficticio para demostrarla.

Errores: `{"error":{"code":"CODIGO","message":"Texto","details":[]}}`. Códigos: `VALIDATION_ERROR` (422), `INVALID_CREDENTIALS` y `UNAUTHENTICATED` (401), `FORBIDDEN` (403), `USERNAME_CONFLICT` y `EMAIL_CONFLICT` (409), `SERVICE_UNAVAILABLE` (503), `INTERNAL_ERROR` (500). Los detalles de validación contienen solo un `field` permitido (`name`, `username`, `email`, `password`, `password_confirmation`, `role`) o `request`, y un mensaje genérico; no repiten entradas, nombres arbitrarios de campos, contraseñas ni tokens. El contrato `/health` no cambia.

El JWT HS256 exige `sub` UUID, `iat`, `exp`, `iss=fittrack-api` y `aud=fittrack-clients`. Expira en 30 minutos por defecto, configurable. No existen refresh tokens ni revocación inmediata. OpenAPI generado por FastAPI documenta las rutas realmente implementadas.

| Método | Ruta | Rol y propósito |
|---|---|---|
| POST | `/auth/register` | Público; registrar usuario y devolver sesión. |
| POST | `/auth/login` | Público; correo/contraseña y sesión. |
| GET | `/users/me` | Ambos; identidad vigente. |
| GET | `/trainers` | Atleta; buscar por username o ID. |
| GET | `/trainers/{trainer_id}` | Atleta; perfil público. |
| POST | `/coaching/requests` | Atleta; crear solicitud pendiente. |
| GET | `/coaching/requests/sent` | Atleta; solicitudes propias. |
| GET | `/coaching/requests/received` | Entrenador; solicitudes recibidas. |
| POST | `/coaching/requests/{id}/accept` | Entrenador destinatario; aceptar y vincular. |
| POST | `/coaching/requests/{id}/reject` | Entrenador destinatario; rechazar. |
| POST | `/coaching/requests/{id}/cancel` | Atleta solicitante; cancelar pendiente. |
| GET | `/coaching/me` | Atleta; vínculo activo. |
| GET | `/coaching/athletes` | Entrenador; atletas activos propios. |
| GET | `/coaching/athletes/{athlete_id}` | Entrenador; detalle de atleta vinculado. |
| POST | `/coaching/relationships/{id}/end` | Participante del vínculo; finalizar. |
| GET | `/routines` | Ambos; filtrado por propiedad o vínculo activo. |
| GET | `/routines/{id}` | Ambos; detalle autorizado. |
| POST | `/routines` | Entrenador; crear con ejercicios. |
| PUT | `/routines/{id}` | Entrenador propietario; editar y reasignar. |
| DELETE | `/routines/{id}` | Entrenador propietario; eliminar. |
| POST | `/routines/{id}/restore` | Entrenador propietario; restaurar, pendiente de estrategia. |

Las rutas de la tabla se anteponen a `/api/v1`. Respuestas privadas con `Authorization: Bearer <token>`, nunca token en URL. Códigos previstos: 200/201/204 éxito; 400 petición inválida; 401 no autenticado; 403 prohibido; 404 inexistente/no visible; 409 conflicto; 422 validación; 500 error inesperado. La estructura uniforme de errores ya se aplica a las rutas actuales; las rutas futuras deben respetarla sin exponer trazas, hashes ni recursos privados.

URL local prevista: `http://10.0.2.2:8000/` desde emulador; `http://127.0.0.1:8000/` desde host. HTTP local solo en debug. Producción: `https://<dominio-o-host-validado>/` por definir en etapa 21; sin IP ni dominio inventados. Configurar BASE_URL en un solo lugar cuando se integre Retrofit. No desactivar validación TLS.

## Extensión v5.0 preliminar

| Método | Ruta completa | Propósito |
|---|---|---|
| PUT | /api/v1/devices/{installation_id} | Registrar/actualizar instalación del usuario autenticado y token FCM de forma segura. |
| DELETE | /api/v1/devices/{installation_id} | Desvincular instalación propia autenticada. |

No existe endpoint público para enviar notificaciones arbitrarias. Los dos endpoints de dispositivos son planificados y no están implementados. FastAPI/PostgreSQL determinarán destinatarios de eventos después de confirmar transacciones.
