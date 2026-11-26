# Plan incremental FitTrack v5.0

Etapa 01 en **revisión final**; etapas 02–22 pendientes. Responsable actual: 253418-m; siguiente: 253699-bot después de revisión e integración Git autorizada. `main` conserva versiones estables y `develop` integra etapas revisadas. Cada etapa usa su rama de trabajo propuesta desde `develop` actualizado. Las ramas no existen aún en este workspace, que no tiene repositorio Git. Los commits deben representar contribuciones reales de cada integrante.

| Etapa | Responsable | Rama | Dependencias | Entrega | Criterio de aceptación | Estado |
|---|---|---|---|---|---|---|
| 01 Base | 253418-m | `chore/project-bootstrap` | Plantilla Android | Android/FastAPI base, MainActivity SRP y documentación v5.0 | Compilación, pruebas, lint, health y documentos verificados | En revisión final |
| 02 Diseño | 253699-bot | `feat/design-system` | 01; referencia visual | 24 vistas planificadas y sistema visual | Estados, accesibilidad e identidad revisados | Pendiente |
| 03 Backend auth | 253699-bot | `feat/backend-auth` | 01 | PostgreSQL local, Alembic, usuarios y JWT | Registro, login, permisos y migraciones probados | Pendiente |
| 04 Android auth | 253418-m | `feat/android-auth` | 02, 03 | Retrofit, sesión, login y roles | Integración local, token protegido y navegación probados | Pendiente |
| 05 Backend coaching | 253418-m | `feat/backend-coaching` | 03 | Búsqueda, solicitudes, vínculos y permisos | Cardinalidad, autorización y concurrencia probadas | Pendiente |
| 06 Entrenador atletas | 253699-bot | `feat/trainer-athletes` | 04, 05 | Atletas y solicitudes recibidas | Aceptación, rechazo y desvinculación probados; rutinas esperan 08 | Pendiente |
| 07 Atleta coaching | 253418-m | `feat/athlete-coaching` | 06 | Búsqueda y vínculo desde atleta | Solicitud, cancelación, estados y abandono probados | Pendiente |
| 08 Backend rutinas | 253699-bot | `feat/backend-routines` | 05 | Migraciones y CRUD de rutinas | Propiedad, integridad y transacciones probadas | Pendiente |
| 09 Atleta rutinas | 253418-m | `feat/athlete-routines` | 07, 08 | Consulta y detalle de rutinas | Acceso de solo lectura y revocación probados | Pendiente |
| 10 Entrenador rutinas | 253699-bot | `feat/trainer-routines` | 06, 08, 09 | CRUD visual y detalle de atleta | Formularios, asignación y errores probados | Pendiente |
| 11 Hábitos | 253418-m | `feat/habits-streaks` | 04 | DataStore, hábitos y rachas | Fechas, progreso y aislamiento por usuario probados | Pendiente |
| 12 Calculadora | 253699-bot | `feat/calculator` | 02, 04 | Calculadora compartida | Fórmulas puras, rangos y UI probados | Pendiente |
| 13 Deshacer | 253699-bot | `feat/routine-delete-undo` | 08, 10; estrategia aprobada | Restauración de rutina | Mismo recurso, permisos y Snackbar probados | Pendiente |
| 14 Háptica | 253418-m | `feat/hardware-haptics` | 10, 11 | Actuador y eventos reales | API 26–30/31+, ausencia y cancelación probadas | Pendiente |
| 15 Biometría | 253699-bot | `feat/hardware-biometric` | 04 | Biometría y sesión criptográfica | Opción, cancelación, Keystore y JWT verificados | Pendiente |
| 16 Pasos | 253418-m | `feat/hardware-step-counter` | 11 | Sensor, permisos y DataStore | Sesiones, fecha, reinicio y alternativa manual probados | Pendiente |
| 17 Backend push | 253699-bot | `feat/backend-push-notifications` | 05, 08 | Instalaciones, outbox y Firebase Admin | Destinatarios, transacciones, reintentos e idempotencia probados | Pendiente |
| 18 Android push | 253418-m | `feat/android-push-notifications` | 04, 17 | FCM, canales y navegación segura | Permisos, recepción, logout y acceso protegido probados | Pendiente |
| 19 Recordatorios | 253699-bot | `feat/daily-reminders` | 11 | WorkManager y preferencias | Trabajo único, fecha, horario y cancelación probados | Pendiente |
| 20 Seguridad | 253418-m | `test/security-integration` | 03–19 | Auditoría e integración local | Roles, concurrencia, hardware y notificaciones probados | Pendiente |
| 21 AWS | 253699-bot | `chore/aws-deployment` | 20; laboratorio y autorización | EC2, PostgreSQL, HTTPS y presupuesto | Backend, FCM, outbox, Android y respaldo verificados | Pendiente |
| 22 Aceptación | 253418-m | `test/final-acceptance` | 21 | E2E, hardware físico, push y evidencia | Criterios oficiales y flujos completos comprobados | Pendiente |

Cada integrante tiene **11 etapas**. Ambos revisan e integran mediante operaciones Git autorizadas, sin atribuir contribuciones ajenas. El cambio de responsable ocurre después de revisar e integrar la etapa anterior; no inicia la etapa 02 automáticamente.
