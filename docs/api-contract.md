# Contrato REST preliminar

**Único endpoint implementado en etapa 01:** `GET /health` → 200 `{"status":"ok"}`. Mide proceso FastAPI, no PostgreSQL. Todas las rutas siguientes son **planificadas** bajo `/api/v1`; OpenAPI de etapas posteriores será la referencia real.

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

Las rutas de la tabla se anteponen a `/api/v1`. Respuestas privadas con `Authorization: Bearer <token>`, nunca token en URL. Códigos previstos: 200/201/204 éxito; 400 petición inválida; 401 no autenticado; 403 prohibido; 404 inexistente/no visible; 409 conflicto; 422 validación; 500 error inesperado. Definir estructura uniforme de error antes de implementar; no exponer trazas, hashes ni recursos privados.

URL local prevista: `http://10.0.2.2:8000/` desde emulador; `http://127.0.0.1:8000/` desde host. HTTP local solo en debug. Producción: `https://<dominio-o-host-validado>/` por definir en etapa 21; sin IP ni dominio inventados. Configurar BASE_URL en un solo lugar cuando se integre Retrofit. No desactivar validación TLS.

## Extensión v5.0 preliminar

| Método | Ruta completa | Propósito |
|---|---|---|
| PUT | /api/v1/devices/{installation_id} | Registrar/actualizar instalación del usuario autenticado y token FCM de forma segura. |
| DELETE | /api/v1/devices/{installation_id} | Desvincular instalación propia autenticada. |

No existe endpoint público para enviar notificaciones arbitrarias. Los dos endpoints son planificados y no están implementados. GET /health continúa siendo el único endpoint actual. FastAPI/PostgreSQL determinan destinatarios de eventos después de confirmar transacciones.
