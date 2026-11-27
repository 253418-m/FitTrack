# Plan incremental FitTrack · alcance v5.1 aprobado

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

Etapa 01 **completada y publicada** en GitHub con commit `16f05f788efcbd199278d0291d2e90f7b7974693`. Existen `main`, `develop` y `chore/project-bootstrap`; `main` es estable y `develop` integra etapas revisadas. La fase 02.1 (mockups) está completada; la fase 02.2 (sistema de diseño) tuvo validación local previa en `feat/design-system`. La revisión 02.3 aprobó compilación, 3 pruebas unitarias, lint y 11 pruebas instrumentadas actuales; el arranque se verificó en Pixel 10 Pro XL AVD. La etapa 02 se publicó e integró en `develop` mediante [PR #1](https://github.com/253418-m/FitTrack/pull/1), merge commit `581bc52523dace75d55b7cedee4c13c05064ef16`. La etapa 03 está implementada y validada localmente en `feat/backend-auth`: PostgreSQL 18.6, migración `bd7cce2c8602`, 48 pruebas iniciales aprobadas y flujo HTTP real comprobado. Fue revisada, publicada e integrada en `develop` por PR #2; no implica inicio de etapa 04. Las etapas 04–22 permanecen pendientes. Los commits deben representar contribuciones reales.

| Etapa | Responsable | Rama | Dependencias | Entrega | Criterio de aceptación | Estado |
|---|---|---|---|---|---|---|
| 01 Base | 253418-m | `chore/project-bootstrap` | Plantilla Android | Android/FastAPI base, MainActivity SRP y documentación v5.0 | Compilación, pruebas, lint, health y documentos verificados | Completada y publicada |
| 02 Diseño | 253699-bot | `feat/design-system` | 01; referencia visual | 24 vistas planificadas y sistema visual | Estados, accesibilidad e identidad revisados | Completada e integrada en develop por PR #1 |
| 03 Backend auth | 253699-bot | `feat/backend-auth` | 01 | PostgreSQL local, Alembic, usuarios y JWT | Registro, login, permisos y migraciones probados | Integrada en develop por PR #2 |
| 04 Android auth | 253418-m | `feat/android-auth` | 02, 03 | Retrofit, sesión, login y roles | Integración local, token protegido y navegación probados | Pendiente |
| 05 Backend coaching | 253418-m | `feat/backend-coaching` | 03 | Búsqueda, solicitudes, vínculos y permisos | Cardinalidad, autorización y concurrencia probadas | Pendiente |
| 06 Entrenador atletas | 253699-bot | `feat/trainer-athletes` | 04, 05 | Atletas y solicitudes recibidas | Aceptación, rechazo y desvinculación probados; rutinas esperan 08 | Pendiente |
| 07 Atleta coaching | 253418-m | `feat/athlete-coaching` | 06 | Búsqueda y vínculo desde atleta | Solicitud, cancelación, estados y abandono probados | Pendiente |
| 08 Backend rutinas | 253699-bot | `feat/backend-routines` | 05 | Migraciones y CRUD de rutinas | Propiedad, integridad y transacciones probadas | Pendiente |
| 09 Atleta rutinas | 253418-m | `feat/athlete-routines` | 07, 08 | Consulta y detalle de rutinas | Acceso de solo lectura y revocación probados | Pendiente |
| 10 Entrenador rutinas | 253699-bot | `feat/trainer-routines` | 06, 08, 09 | CRUD visual y detalle de atleta | Formularios, asignación y errores probados | Pendiente |
| 11 Hábitos | 253418-m | `feat/habits-streaks` | 04 | Plan histórico: DataStore, hábitos y rachas; sustituido por 11.A/11.B v5.1 | Fechas, progreso y aislamiento por usuario | Pendiente |
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

Cada integrante tiene **11 etapas**. Ambos revisan e integran mediante operaciones Git autorizadas, sin atribuir contribuciones ajenas. El cambio de responsable tras etapa 01 fue autorizado. La etapa 03 fue integrada; el commit de implementación corresponde a 253699-bot. La etapa 04 corresponde a 253418-m y no inicia automáticamente.

## Revisión propuesta v5.1 · 27 de noviembre de 2026

Los párrafos anteriores describen el plan histórico v5.0. El estado vigente es: 01, 02 y 03 **cerradas**; PR #1 merge `581bc52523dace75d55b7cedee4c13c05064ef16`, PR #2 merge `12df7874276bece8e682a5540806238f2145fff1`. 03.R.1 fue auditoría de solo lectura; 03.R.2 es propuesta documental sin commit; 03.R.2.1 corrige trazabilidad y contratos sin commit; 03.R.3 registró y revisó la dirección visual base de 13 referencias nuevas; cobertura Must parcial. La etapa 04 sigue pendiente. P03 hábitos remotos está aprobado por D01.

| Etapa y rama | Objetivo y dependencia | Responsable planificado | HU y aceptación / pruebas y evidencia | Estado |
|---|---|---|---|---|
| 01 · chore/project-bootstrap | Base y arquitectura | Integrante 1 | Repositorio, compilación, pruebas iniciales | Cerrada |
| 02 · feat/design-system | Referencias y Material 3; 01 | Integrante 2 | Componentes, capturas, regresión | Cerrada |
| 03 · feat/backend-auth | PostgreSQL, JWT, Argon2id; 02 | Integrante 2 | HU01–02, componente backend; PostgreSQL, Alembic, 48 pruebas iniciales | Cerrada |
| 03.R.1 · sin rama | Auditar ambos PDF; 03 | Compartida por confirmar | Matriz de brechas y fuentes | Cerrada |
| 03.R.2 · docs/rubric-alignment | Procesos, HU, RF y trazabilidad; 03 | Por confirmar | Propuesta documental elaborada, pendiente de aprobación | Elaborada |
| 03.R.2.1 · docs/rubric-alignment | Corregir HU, RF, API y Room; 03.R.2 | Por confirmar | 56 RF y 32 HU tras HU32; enlaces y trazabilidad revisados | Corregida |
| 03.R.3 · docs/rubric-alignment | Inventariar 13 PNG/13 SVG y 25 vistas; 03.R.2.1 | Por confirmar | Diseño base revisado, 26 HU Must mapeadas; faltan estados obligatorios y aprobación | Registro completo; cobertura parcial |
| 04 · feat/android-auth | Sesión, Navigation, roles; integración 03.R.8 y revisión de wireframes Must | Integrante 1 | HU01–05,22; UseCase/ViewModel, 401 y logout | Pendiente |
| 04.B · feat/contact | API y pantalla Contactame; 04 | Integrante 2 aprobado | HU06–07; HTTP real, VM, Intent | Aprobada; implementación pendiente |
| 05 · feat/backend-coaching | Solicitudes y vínculo; 03 | Integrante 1 | HU08–12; permisos, concurrencia y transacciones | Pendiente |
| 06 · feat/trainer-athletes | Bandeja y atletas; 05 | Integrante 2 | HU10,13; aceptación y lista | Pendiente |
| 07 · feat/athlete-coaching | Búsqueda y vínculo; 05–06 | Integrante 1 | HU08–12/HU31–HU32; P01 extremo a extremo | Pendiente |
| 08 · feat/backend-routines | Recursos y propiedad; 05 | Integrante 2 | HU14–16; CRUD y autorización PostgreSQL | Pendiente |
| 09 · feat/athlete-routines | Consulta de asignaciones; 08 | Integrante 1 | HU15; estados carga/vacío/error | Pendiente |
| 09.B · feat/routines-cache | Room y reconexión; 09 | Integrante 1 aprobado | HU17; DAO, aislamiento, revocación, offline | Aprobada; implementación pendiente |
| 10 · feat/trainer-routines | Creación, edición, borrado UI; 08 | Integrante 2 | HU13–16/HU30; P02 extremo a extremo | Pendiente |
| 11.A · feat/backend-habits | API y persistencia P03; aprobación | Integrante 2 aprobado | HU18–19/HU25; fecha, autorización, concurrencia | Aprobada; implementación pendiente |
| 11.B · feat/habits-streaks | Hoy y rachas remotas; 11.A | Integrante 1 aprobado | HU18–19/HU25; estados y cálculo consistente | Aprobada; implementación pendiente |
| 12 · feat/calculator | Calculadora corporal | Integrante 2 | HU24; fórmulas y validaciones | Pendiente · Should |
| 13 · feat/routine-delete-undo | Deshacer eliminación; 10 | Integrante 2 | HU27; recuperación verificable | Pendiente · Should |
| 14 · feat/hardware-haptics | Confirmar meta guardada; 11.B | Integrante 1 | HU21; fake y dispositivo físico | Pendiente |
| 15 · feat/hardware-biometric | Desbloqueo voluntario; 04 | Integrante 2 | HU22; cancelación y fallback físico | Pendiente |
| 16 · feat/hardware-step-counter | Pasos observados; 11.B | Integrante 1 | HU20; permisos, baseline y dispositivo físico | Pendiente |
| 17 · feat/backend-push-notifications | Devices, Firebase Admin, eventos; 05 y 08 | Integrante 2 | HU23; dobles y entrega real | Pendiente |
| 18 · feat/android-push-notifications | FCM, canal, navegación; 17 | Integrante 1 | HU23; dos dispositivos y destino protegido | Pendiente |
| 19 · feat/daily-reminders | Recordatorios y preferencias; 11.B | Integrante 2 | HU26/HU29; reglas de fecha, permiso y cancelación | Pendiente · Should |
| 20 · test/security-integration | Seguridad y tres procesos; 04–19 | Integrante 1 | HU Must; 401/403, tres flujos, evidencias | Pendiente |
| 21 · chore/aws-deployment | EC2, PostgreSQL, Nginx HTTPS; 20 | Integrante 2 | URL real, TLS, backup, seguridad | Pendiente |
| 22 · test/final-acceptance | Nueve criterios; 21 | Integrante 1 | Historias Must, hardware físico, rúbrica | Pendiente |

Las asignaciones revisadas de las 22 etapas principales conservan la distribución original de la tabla histórica; 11.A/11.B desglosan la etapa 11 sin cambiar su responsable principal 253418-m. Las subetapas nuevas son propuestas sujetas a acuerdo del equipo. Cada PR de feature entra en `develop`. Se prevén integraciones controladas a `main` tras 07, 13, 19 y 22, condicionadas por pruebas y revisión; esos hitos no acreditan por sí solos el MVP. Ver [HU](user-stories.md), [procesos](business-processes.md) y [rúbrica](rubric-compliance.md).

**Cierre documental 03.R.3:** el registro visual y la inspección PNG están completos; la cobertura visual de las 26 HU Must es parcial y se resolverá antes de acreditar C01/C02. La aceptación general de estilo no cierra estados faltantes ni aprueba P03, política offline, medio de contacto o biometría docente. La etapa 04 no comenzó. No se generaron ni versionaron imágenes.

## Complemento documental 03.R.3.1

Registro de cuatro variantes completado: 03.E/03.F/03.G y 19.B. Se preservan 22 etapas principales y responsables; 01–03 cerradas. La etapa 03.R.3 tiene registro de 17 PNG/17 SVG y diseño base revisado, con cobertura complementaria parcial. Antes de acreditar cobertura completa deben resolverse las brechas del [inventario](design/mockup-inventory.md); no se requiere otra pantalla independiente por cada estado reutilizable. Sin nuevas funcionalidades ni inicio de etapa 04. Se mantienen 56 RF, 32 HU y 25 vistas únicas; las decisiones de alcance y distribución adicional siguen pendientes.

## Formalización 03.R.4 y secuencia posterior

03.R.4: alcance v5.1 y asignaciones aprobados por el usuario; documentación formalizada sin commit. 03.R.3 conserva registro completo y cobertura visual parcial. Las 22 asignaciones principales permanecen vigentes.

| Subetapa | Rama | Responsable aprobado |
|---|---|---|
| 04.B | feat/contact | 253699-bot |
| 09.B | feat/routines-cache | 253418-m |
| 11.A | feat/backend-habits | 253699-bot |
| 11.B | feat/habits-streaks | 253418-m |

La autoría Git se confirmará por contribuciones reales. Secuencia requerida: **03.R.5 auditoría/commit documental → 03.R.6 push → 03.R.7 PR hacia develop → 03.R.8 merge/sincronización → 04 autenticación Android**. La etapa 04 depende de integración documental previa y revisión de sus wireframes Must. Ninguna operación posterior queda ejecutada ni autorizada automáticamente por esta formalización.
