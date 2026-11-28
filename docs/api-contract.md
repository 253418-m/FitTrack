# Contrato REST de FitTrack

**Estado vigente R1–R2 · FitTrack Gym v6.0 (27-11-2026):** alcance aprobado por el usuario y aclaraciones docentes aportadas por él. Sustituye expresamente las reglas incompatibles v5.1 de registro libre/selección de rol, cinco hábitos/meta 3 de 5 y cumplimiento manual de pasos. Las secciones previas incompatibles se conservan como evidencia histórica, sin vigencia normativa. No se atribuye una inspección nueva de material docente. Implementación actual: autenticación e invitaciones; procesos de negocio, Room, FCM, hardware, Contactame y HTTPS siguen pendientes.

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

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

## Extensión histórica v5.0 (revisada por la propuesta v5.1)

| Método | Ruta completa | Propósito |
|---|---|---|
| PUT | /api/v1/devices/{installation_id} | Registrar/actualizar instalación del usuario autenticado y token FCM de forma segura. |
| DELETE | /api/v1/devices/{installation_id} | Desvincular instalación propia autenticada. |

No existe endpoint público para enviar notificaciones arbitrarias. Los dos endpoints de dispositivos son planificados y no están implementados. FastAPI/PostgreSQL determinarán destinatarios de eventos después de confirmar transacciones.

## Extensión v5.1 propuesta — rutas no implementadas

**Fecha:** 27-11-2026. Se conserva íntegro el contrato implementado de auth y `/health` descrito arriba. **Solo esas cuatro rutas de negocio existen actualmente en OpenAPI.** Las rutas de esta sección son planificadas o propuestas; sus cuerpos definitivos y políticas abiertas requieren aprobación. JSON usa snake_case, fechas ISO 8601 con zona, UUID y errores `{"error":{"code":"…","message":"…","details":[]}}`. No colocar JWT en URL. [RF](requirements.md) · [HU](user-stories.md) · [procesos](business-processes.md).

| ID | Método y ruta bajo /api/v1 | Rol/propiedad; parámetros y cuerpo | Éxito; errores; efecto/evento | HU; etapa; estado |
|---|---|---|---|---|
| E01 | GET /trainers?query= | ATHLETE; username o UUID, sin email privado | 200 lista; 401/403/422/503 | HU08; 05; PLANNED |
| E02 | GET /trainers/{trainer_id} | ATHLETE; perfil público mínimo | 200; 401/403/404 | HU08; 05; PLANNED |
| E03 | POST /coaching/requests | ATHLETE; trainer_id UUID | 201 PENDING; 401/403/404/409/422; evento recibido | HU09; 05; PLANNED |
| E04 | GET /coaching/requests/sent | ATHLETE propio | 200 lista/estado; 401/403 | HU09/HU11/HU31; 05; PLANNED |
| E05 | GET /coaching/requests/received | TRAINER destinatario | 200 lista; 401/403 | HU10; 05; PLANNED |
| E06 | POST /coaching/requests/{id}/accept | TRAINER destinatario; sin body | 200 vínculo; 401/403/404/409; evento aceptado | HU10; 05; PLANNED |
| E07 | POST /coaching/requests/{id}/reject | TRAINER destinatario | 200 estado; 401/403/404/409; evento rechazado | HU10; 05; PLANNED |
| E08 | POST /coaching/requests/{id}/cancel | ATHLETE solicitante | 200 estado; 401/403/404/409 | HU11; 05; PLANNED |
| E09 | GET /coaching/me | ATHLETE propio | 200 vínculo/estado; 401/403 | HU31; 05; PLANNED |
| E10 | GET /coaching/athletes | TRAINER propio | 200 lista; 401/403 | HU13; 05; PLANNED |
| E11 | GET /coaching/athletes/{athlete_id} | TRAINER del vínculo | 200 detalle mínimo; 401/403/404 | HU13; 05; PLANNED |
| E12 | POST /coaching/relationships/{id}/end | Participante propio | 200 fin; 401/403/404/409; revoca acceso | HU12; 05; PLANNED |
| E13 | GET /routines | Ambos; servidor filtra propietario/vínculo | 200 lista; 401/403/503 | HU15/HU17/HU30; 08; PLANNED |
| E14 | GET /routines/{id} | Dueño o atleta vinculado | 200 rutina y ejercicios; 401/403/404 | HU15/HU17/HU30; 08; PLANNED |
| E15 | POST /routines | TRAINER; vínculo activo, datos y ≥1 ejercicio | 201; 401/403/409/422; evento asignada | HU14; 08; PLANNED |
| E16 | PUT /routines/{id} | TRAINER propietario; cuerpo completo válido | 200; 401/403/404/409/422 | HU16; 08; PLANNED |
| E17 | DELETE /routines/{id} | TRAINER propietario | 204; 401/403/404/409; revoca lectura | HU16; 08; PLANNED |
| E18 | POST /routines/{id}/restore | TRAINER propietario, solo si Deshacer aprobado | 200; 401/403/404/409 | HU27; 13; PROPOSED |
| E19 | POST /contact | ATHLETE/TRAINER; JWT; solo subject y message | 201 id/created_at; 401/422/429/503; persiste mensaje propio | HU06; 04.B; PLANNED |
| E20 | PUT /devices/{installation_id} | Ambos; instalación propia, token FCM | 200; 401/403/422; asocia/actualiza | HU23; 17; PLANNED |
| E21 | DELETE /devices/{installation_id} | Ambos; instalación propia | 204; 401/403/404; desasocia | HU04/HU23; 17; PLANNED |
| E22 | GET /habits/daily/{date} | ATHLETE propio; fecha ISO local | 200 día/progreso; 401/403/422 | HU19/HU26; 11.A; PLANNED |
| E23 | PUT /habits/daily/{date} | ATHLETE propio; creación o reemplazo completo, versión esperada | 201 creación / 200 reemplazo; 401/403/409/422/503 | HU18/HU20; 11.A; PLANNED |
| E24 | GET /habits/history?from=&to= | ATHLETE propio; rango acotado | 200 días y rachas; 401/403/422 | HU19/HU25; 11.A; PLANNED |
| E25 | DELETE /habits/daily/{date} | ATHLETE propio; versión esperada | 204 tras borrar; 401/403/404/409; recalcula historial | HU18/HU19; 11.A; PLANNED |

### Contratos nuevos propuestos, sin alterar autenticación

**Contacto E19.** Acceso autenticado de ATHLETE o TRAINER desde Ajustes. Solicitud `{"subject":"Consulta sobre mi cuenta","message":"Necesito orientación sobre una rutina."}`; ambos campos requeridos, recortados, longitudes 1–120 y 1–2000; campos extra rechazados. `sender_id` UUID se toma del JWT, nunca del cuerpo. Nombre y correo se muestran precargados y no editables en Android; el servidor obtiene la identidad actual y puede guardar una instantánea `sender_name`/`sender_email` junto a `sender_id` para auditoría. `201 {"id":"00000000-0000-4000-8000-000000000001","created_at":"2026-11-27T00:00:00Z"}` solo tras commit. 401 `UNAUTHENTICATED` sin sesión, 422 `VALIDATION_ERROR`, 429 `RATE_LIMITED` propuesto, 503 `SERVICE_UNAVAILABLE`; todos usan envoltura de error uniforme y 401 conserva `WWW-Authenticate: Bearer`. Guardar no implica entrega por correo. ACTION_SENDTO `mailto:` requiere dirección real autorizada, pendiente; acceso público desde Login no aprobado. Retención aprobada D07 durante operación y evaluación MVP, con eliminación posterior controlada.

**Hábitos E22–E25.** `{date}` es fecha local ISO `YYYY-MM-DD` acompañada por `time_zone` IANA validada; servidor deriva propietario de JWT. `GET` del día ausente devuelve 200 `{"exists":false,"date":"2026-11-27","habits":{"water":false,"walk":false,"sleep":false,"train":false,"stretch":false},"observed_steps":0,"completed_count":0,"goal_met":false,"version":null}`; ausencia no es 404 ni fallo de red. Si existe, `exists:true` y `version` entero positivo. `GET /history` devuelve días confirmados dentro de un rango acotado, racha actual y mejor derivadas de esos días, con paginación/retención por definir.

`PUT /habits/daily/{date}` es **reemplazo completo** del estado del día, creación si `expected_version:null` y no existe, o actualización/corrección si coincide la versión; no se propone PATCH en v5.1. Ejemplo técnico preliminar a completar en 11.A con UUID de instancia además de versión (no constituye contrato final concurrente): `{"time_zone":"America/Mexico_City","habits":{"water":true,"walk":false,"sleep":true,"train":false,"stretch":true},"observed_steps":1200,"steps_source":"device_current","expected_version":1}`. Cinco booleanos requeridos; pasos entero ≥0 y ≤límite técnico por aprobar. `steps_source` distingue observación del sensor de marca manual. El servidor no suma lecturas ni contadores de distintos dispositivos; el cliente envía el valor absoluto observado del dispositivo activo para ese día y un cambio de dispositivo exige recarga y confirmación explícita. La API devuelve `201` al crear o `200` al reemplazar, con representación confirmada, `completed_count`, `goal_met`, `updated_at` UTC y nueva versión. `409 VERSION_CONFLICT` propuesto indica versión distinta: recargar y mostrar conflicto, sin reintento ciego. Reenvío idéntico después de perder respuesta puede devolver 409; el cliente consulta y compara el estado confirmado antes de decidir si repetir. Así no se promete idempotencia de transporte no implementada.

`DELETE /habits/daily/{date}` restablece el día eliminando el registro propio completo, con `expected_version` como parámetro obligatorio; `204` tras commit, `404 NOT_FOUND` propuesto si no existe y `409 VERSION_CONFLICT` si la versión cambió. El día queda sin registro y rachas/historial se recalculan; las correcciones ordinarias de un hábito se hacen con PUT, no con DELETE. D04 exige borrado físico; no aplica a rutinas. La meta sigue siendo ≥3 de 5. No hay escritura offline ni segundo recurso de completaciones.

**Rutas restantes.** E01–E02 exponen solo `id UUID`, `username`, `name` y datos públicos aprobados; `email`, hash y atletas nunca aparecen. E03–E12 usan UUID de solicitud/vínculo, estados `PENDING/ACCEPTED/REJECTED/CANCELLED`, transición y propiedad atómicas; 409 para transición o vínculo incompatible. E13–E17 devuelven rutinas con `id UUID`, `trainer_id UUID`, `athlete_id UUID`, `name string` 1–100, `group string`, `level string`, `duration_minutes integer >0`, `exercises[]` con `name`, `sets integer >0`, `repetitions integer >0` y `position integer >=0`; campos de grupo/nivel y límites finales se aprobarán antes de implementar. GET filtra por propietario/vínculo vigente; POST/PUT exigen entrenador propietario y atleta vinculado, 409 ante vínculo caducado, 422 ante campos inválidos. E18 restauración solo si se aprueba Deshacer. E20–E21 asocian `installation_id UUID` y token FCM no vacío con la identidad del JWT; respuesta nunca devuelve token, rotación reemplaza asociación y logout la elimina. Preferencias locales no son endpoint. Todos los eventos push se originan **después** de la transacción de negocio; su entrega no define el éxito de la operación.

No hay claves, tokens ni contraseñas reales en estos ejemplos. `GET /health` no prueba PostgreSQL. Las rutas futuras no aparecen actualmente en OpenAPI.

**D04 aprobada:** DELETE elimina físicamente el registro diario propio tras confirmación y control optimista. Para evitar ABA al borrar y recrear, las mutaciones deben comparar identidad de instancia del registro (UUID no reutilizable) y versión; una recreación obtiene UUID nuevo, y una petición con identidad antigua devuelve 409. No reiniciar una versión como única prueba de identidad. El detalle técnico deberá concretarse y probarse en 11.A antes de implementar, sin cambiar auth existente.

## Contrato vigente v6.0 · registro por invitación (IMPLEMENTED)

Esta sección sustituye el cuerpo de registro libre descrito históricamente. POST /api/v1/auth/register recibe exclusivamente:
```json
{"name":"Persona de prueba","username":"persona_demo","email":"demo@example.com","password":"solo-ejemplo-no-operativo","password_confirmation":"solo-ejemplo-no-operativo","invitation_code":"codigo-ilustrativo-no-valido"}
```
Se conservan validaciones de identidad y contraseña. invitation_code: 16–128 caracteres, secreto individual; Android lo conserva solo en memoria. role se rechaza como campo extra (422); el rol procede de la invitación. Éxito 201 con access_token/token_type/expires_in/user sin cambios. Código incorrecto/expirado/revocado/consumido: 400 INVALID_INVITATION genérico. Cupo agotado/reducido: 409 GYM_CAPACITY_REACHED. Se preservan 409 de unicidad, 422, 503 y envoltura error. Login y /users/me mantienen rutas/cuerpos; cuenta inactiva recibe el mismo 401 genérico que credenciales/token inválidos. No se añaden rutas administrativas HTTP.

Contratos futuros PLANNED: catálogo filtra TRAINER activo; asignación incluye scheduled_date y unicidad (athlete_id, scheduled_date); cumplimiento se autoriza por atleta asignado y se consulta por propietario entrenador. P03 recibirá solo los tres hábitos editables water/sleep/stretch, más observación de pasos por vía específica; walk_completed será derivado, nunca booleano editable. Endpoints/payload exactos de rutinas/cumplimiento/observaciones deben cerrarse en sus bloques antes de implementarse; los cuerpos v5.1 de cinco booleanos quedan históricos. Sin hardware no hay cumplimiento manual del objetivo de pasos.
