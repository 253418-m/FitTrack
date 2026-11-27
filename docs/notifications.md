# Notificaciones planificadas

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

Estado: plan histórico v5.0, revisado por la política v5.1 inferior; FCM y WorkManager no están incorporados.

## Push remotas

Plan histórico: FCM será transporte. FastAPI/PostgreSQL decide destinatarios; si se aprueba el outbox, registra eventos en notification_outbox dentro de la transacción autorizada: TRAINER_REQUEST_RECEIVED, TRAINER_REQUEST_ACCEPTED, TRAINER_REQUEST_REJECTED y ROUTINE_ASSIGNED. Un procesador sencillo posterior al commit enviará mediante Firebase Admin SDK con reintentos/idempotencia; no garantiza entrega inmediata ni envía eventos revertidos. device_registrations asociará installation_id/token a usuario autenticado y evitará duplicados activos. PUT/DELETE /api/v1/devices/{installation_id} son contratos preliminares, sin implementación. No endpoint público de envío libre.

Android pedirá POST_NOTIFICATIONS desde API 33 en contexto y creará canales desde API 26. Permiso rechazado no impide usar FitTrack. Verificará identidad/permisos al recibir y abrir, no expondrá secretos ni contenido privado en pantalla bloqueada, no registrará tokens en logs y desvinculará instalación/notificaciones al logout, incluso ante desconexión con reconciliación posterior. Credenciales Firebase Admin solo en servidor; no Firebase Auth ni Firestore.

## Recordatorios locales

WorkManager programará un trabajo único por usuario para hábitos pendientes, con hora inicial propuesta 20:00 local configurable y opción de desactivar. El worker consultará progreso remoto confirmado y fecha; si no puede verificarlo, **omite el recordatorio basado en meta pendiente** en vez de afirmar falsamente que faltan hábitos. No notificará si el día ya cumple la meta vigente de tres hábitos. Manejará zona horaria/fecha y duplicados, y cancelará trabajo al logout. No usar alarmas exactas ni prometer hora exacta.

Implementación, permisos, modelos y migraciones corresponden a etapas 17–19.

## Política v5.1 propuesta

FCM es transporte remoto; FastAPI y PostgreSQL deciden destinatario autorizado. Firebase Admin SDK vive solo en backend. Eventos: `TRAINER_REQUEST_RECEIVED`, `TRAINER_REQUEST_ACCEPTED`, `TRAINER_REQUEST_REJECTED`, `ROUTINE_ASSIGNED`. Registrar `device_registrations` tras login, actualizar por rotación de token, desasociar en logout y impedir notificaciones cruzadas al cambiar cuenta. Android 13+ solicita `POST_NOTIFICATIONS`, crea canal propio y abre destino protegido tras validar sesión; payload y pantalla bloqueada minimizan datos privados.

Distinguir evento persistido, aceptación por FCM, recepción y apertura; ninguno implica automáticamente el siguiente. El outbox transaccional es una opción de fiabilidad sujeta a evaluación, no exigencia literal de la rúbrica. Pruebas con dobles Firebase Admin y una aceptación real con dispositivos cuando exista infraestructura. WorkManager es recordatorio **local**, no sustituto de FCM; debe consultar progreso remoto reciente o explicitar datos desactualizados antes de decidir. Ver [modelo](data-model.md) y [rúbrica](rubric-compliance.md).

**Revisión 03.R.2.1:** RF-31–RF-35 y RF-38–RF-39 (registro de dispositivo, canal, permiso aplicable y destino protegido) son Must para FCM; solo preferencias avanzadas son Should. La confirmación de la operación P01/P02 depende de PostgreSQL, nunca de entrega inmediata por FCM. El outbox sencillo sigue propuesto, no implementado.
