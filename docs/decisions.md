# Decisiones y asuntos pendientes · FitTrack v5.0

## Requisitos confirmados por el propietario

MainActivity cumple responsabilidad única: aloja Compose, el tema y FitTrackApp, sin lógica de negocio. Son obligatorias API REST propia, FastAPI, PostgreSQL en la **misma instancia EC2** que la API, presupuesto académico de **50 USD**, dos roles y vínculo formal, CRUD de rutinas, integración de hardware solicitada por el docente y notificaciones push, hábitos locales y calculadora en memoria. Un solo módulo Android inicialmente. La exclusión anterior de Room es histórica y contradice la rúbrica oficial de Unidad 4; su incorporación queda pendiente de una decisión de alcance. No RDS, microservicios, Firebase como sustituto de la API ni aplicaciones separadas por rol. En etapa 01 solo se implementan arranque local y `GET /health`.

## Políticas de negocio propuestas para validar

1. Máximo una solicitud pendiente por atleta, con índice parcial.
2. Atleta vinculado no envía otra solicitud; pendiente puede cancelarse.
3. Rutina conserva referencia al vínculo por el que fue asignada.
4. Terminar vínculo revoca acceso del atleta a esas rutinas; entrenador mantiene propiedad.
5. Rutina de vínculo terminado requiere reasignación activa antes de editar.
6. Nuevo vínculo con entrenador anterior no reactiva rutinas antiguas.
7. Eliminación lógica `deleted_at` y restauración del mismo registro para Deshacer.
8. Meta diaria de hábitos: tres de cinco; conservar historial para mejor racha.
9. Servidor asigna fechas remotas, incluida creación de rutina inmutable.
10. UUID como ID público; en etapa 03 se adoptó username ASCII de 3–30 caracteres (`a-z`, `0-9`, punto, guion bajo), minúsculas y espacios externos eliminados; correo validado, minúsculas y máximo 254 caracteres.
11. Rangos seguros para peso, estatura y edad adulta por definir.
12. En etapa 03 se adoptó `error: {code, message, details}` y JWT HS256 de 30 minutos configurables, con issuer/audience fijos y clave externa de mínimo 32 bytes. No hay refresh ni revocación inmediata.

## Implementaciones técnicas propuestas dentro del plan v5.0

Para cumplir la integración de hardware se proponen tres familias: actuador, respuesta única y flujo continuo. La selección concreta se validará al implementar cada etapa.

- Actuador: vibración háptica con `VibratorManager`/ `Vibrator` según API, solo por eventos reales y con cancelación.
- Respuesta única: biometría opcional con AndroidX Biometric y Keystore; no reemplaza login/JWT remoto.
- Flujo continuo: `TYPE_STEP_COUNTER` mediante `SensorManager` y `callbackFlow`, solo durante **sesiones activas**, con alternativa manual para el hábito de caminar 8,000 pasos. No se promete seguimiento de 24 horas.
- Push: Firebase Cloud Messaging como transporte, Firebase Admin solo en servidor, `device_registrations` y `notification_outbox` propuestos para entregas tras commit e idempotencia.
- Recordatorios: WorkManager local por usuario con horario inicial propuesto de **20:00** configurable; sin garantía de hora exacta. Las preferencias y el cierre de sesión controlarán los trabajos.
- 24 vistas de diseño son inventario planificado, no pantallas implementadas.

## Contradicciones y fuentes

El contexto v5.0 sustituye exclusiones anteriores de sensores, push y WorkManager. Las solicitudes entre entrenador y atleta son obligatorias y una rutina solo puede asignarse a un atleta vinculado. Los diez mockups originales no cubren el alcance actual. El PDF aprobado y las exportaciones SVG/PNG están en el directorio hermano local `FitTrack-references`, fuera del repositorio. El tema de etapa 02.2 es oscuro fijo, sin colores dinámicos, con tokens centralizados y componentes Compose sin lógica de negocio. Los roles Material 3 se declaran explícitamente para evitar colores predeterminados ajenos. El rojo de marca `#DC2626` permanece como fondo del botón principal con texto blanco; `colorScheme.primary` usa `#FF7777` para texto y controles sobre superficies oscuras (6.33:1 sobre `#202020`, 5.65:1 sobre `#292929`). `onPrimary` usa fondo oscuro. El tema busca una Activity a través de ContextWrapper para configurar barras del sistema sin conversión directa insegura. `#777777` se reserva para bordes interactivos con contraste mínimo 3:1 en las tres superficies; `#353535` se conserva para divisores decorativos. El tema fuerza iconos claros en las barras del sistema. Se priorizan las páginas 4 y 20 sobre sus variantes; los glifos de Preview no definen la iconografía final. Los hallazgos sobre el repositorio docente fueron aportados por el supervisor y no se atribuyen a inspección directa en esta revisión. Ricetta y repositorios docentes no se modifican.

## Riesgos y pendientes técnicos

- Concurrencia al aceptar solicitudes, desvincular y asignar: restricciones y transacciones PostgreSQL con pruebas reales.
- DataStore no cifra: token protegido con Keystore y limpieza de estado al cerrar sesión.
- La advertencia **Starlette/httpx** se resolvió en etapa 03 usando `httpx2==2.16.2` solo en pruebas, conforme a la documentación actual de Starlette; la suite PostgreSQL real pasó sin esa advertencia. La primera ejecución aprobó 48 pruebas sin omisiones; la repetición sobre base ya migrada omitió solo el segundo downgrade por seguridad.
- Verificar permisos IAM, región, vigencia, tipos de instancia, precios y saldo antes de crear AWS. `t3.small` es opción, no recurso elegido.
- Una EC2 es punto único de fallo y exige respaldo/restauración externos autorizados. EBS, IPv4, transferencia, snapshots y créditos CPU pueden generar cargos.
- HTTPS exige nombre y certificado confiables; no desactivar validación TLS.
- La etapa 01 se publicó en GitHub con commit `16f05f788efcbd199278d0291d2e90f7b7974693`. La etapa 02.2 se publicó e integró en `develop` mediante PR #1, merge commit `581bc52523dace75d55b7cedee4c13c05064ef16`; la revisión 02.3 había aprobado compilación, 3 pruebas unitarias, lint y 11 pruebas instrumentadas en Pixel 10 Pro XL AVD. El arranque actual se verificó y la captura final se conserva fuera del repositorio. La prueba de contraseña se ajustó para distinguir `InputText` editable de `EditableText` enmascarado. Quedan pendientes la comprobación en dispositivo físico y TalkBack. La autoría real de la etapa 02 fue confirmada por el propietario como `253699-bot`; la identidad se configuró solo en este repositorio. El push de etapa 02 ya se realizó; la etapa 03 no tiene commit ni push.
- RDS se excluye por requisito y presupuesto. La omisión anterior de Room queda pendiente de corrección conforme a la rúbrica oficial; no se implementa durante este commit de autenticación.

## Decisiones técnicas de etapa 03

- SQLAlchemy 2 síncrono con psycopg 3; rutas `def` para operaciones bloqueantes. Sesión nueva por solicitud, commit/rollback desde servicio, cierre garantizado.
- Modelo único `users`; UUID y tiempos UTC asignados por servidor, `TIMESTAMPTZ`, restricciones UNIQUE y CHECK reales. Migración Alembic `bd7cce2c8602` aplicada en `fittrack_dev` y verificada con upgrade/downgrade/upgrade inicial en `fittrack_test`; ambas bases quedaron en head.
- Argon2id de 19 MiB, 2 iteraciones y paralelismo 1 según mínimo recomendado por OWASP. Sal aleatoria de la biblioteca; límite de contraseña 8–128 caracteres sin truncamiento.
- JWT HS256 con clave externa, firma, expiración, emisor, audiencia y claims obligatorios; `sub` UUID. La base sigue siendo autoridad para el rol. Hash ficticio para reducir diferencia temporal cuando no existe correo; no elimina toda filtración temporal. Protección contra intentos repetidos corresponde a endurecimiento posterior.
- `/health` no depende de PostgreSQL. No hay migraciones automáticas. `TEST_DATABASE_URL` se verifica antes de cualquier operación de integración y debe apuntar a `fittrack_test` local, diferente de desarrollo, con rol no superusuario.
- PostgreSQL 18.6 local responde en 127.0.0.1:5432, pero el servicio ya escucha también en 0.0.0.0 y ::. No se cambió su configuración; revisar alcance de red/firewall antes de considerar la instalación segura. Ambas bases FitTrack y sus roles propietarios se verificaron con servidor 18.6 real; no tienen SUPERUSER, CREATEDB, CREATEROLE, REPLICATION, BYPASSRLS ni CONNECT cruzado. El firewall no pudo leerse sin privilegios administrativos y `pg_hba_file_rules` denegó consulta (SQLSTATE 42501); no se comprobó exposición externa.
- `email-validator` rechaza dominios reservados `.test` en los ejemplos de prueba; se usan direcciones ficticias `example.com` para las pruebas. Dos usuarios ficticios creados durante validación HTTP permanecen en `fittrack_dev`; no se borraron usuarios existentes.
- En etapa 03.2 se limitó la contraseña de login a 128 caracteres sin normalización, se restringieron los nombres de campo en errores a una lista permitida, y SQLAlchemy/Alembic ocultan parámetros de consultas en sus errores. `PublicUser` incluye correo únicamente para el usuario autenticado y no es un esquema válido para futuros perfiles públicos. El control de intentos repetidos de login sigue pendiente antes del despliegue.
