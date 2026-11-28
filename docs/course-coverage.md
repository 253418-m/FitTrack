# Cobertura académica FitTrack · alcance v5.1 aprobado

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

**Evidencia existente de etapa 01:** MainActivity delega la UI a FitTrackApp (SRP); la revisión previa reportó compilación Android, una prueba unitaria, lint, dos pruebas backend de health, pip check y compileall satisfactorios. Esa evidencia no demuestra funcionalidades futuras ni cumplimiento de rúbrica. Registrar archivos, pruebas y resultados reales conforme se ejecuten las etapas.

| Fuente | Aplicación prevista | Etapa y evidencia pendiente |
|---|---|---|
| Evaluación I | Calculadora local: Compose, ViewModel, StateFlow, funciones puras y sliders | 12; fórmulas y UI |
| Evaluación II | Propuesta P03: hábitos remotos, DataStore auxiliar, Flow, pasos por sesiones y recordatorios Should | 11.A/B, 16, 19; fecha, aislamiento, sensor y trabajos |
| Evaluación III | API REST, JWT, PostgreSQL, Hilt/Retrofit, vínculos, CRUD y notificaciones remotas | 03–10, 13, 17–18, 20–22; backend, red, UI, seguridad y E2E |
| Android Basics 1 | Kotlin, recursos, estructura Android, Compose y SRP de MainActivity | 01; evidencia local previa de compilación y código |
| Android Basics 2 | Estado, formularios, validaciones y pruebas de lógica | 04, 10, 12; pruebas pendientes |
| Android Basics 3 | Colecciones, LazyColumn, Material y accesibilidad | 02: tema, componentes, tipografía y previews implementados; listas y pantallas completas pendientes en 06–12 |
| Android Basics 4 | ViewModel, Lifecycle, StateFlow, navegación tipada y UDF | 04–12, 18; pruebas de navegación/estado pendientes |
| Android Basics 5 | Corrutinas, Retrofit, JSON, repositorios, errores y Hilt | 04–10, 17–18; MockWebServer e integración pendientes |
| Android Basics 6 | Preferences DataStore, Room para rutinas offline e integridad PostgreSQL remota | 03–04, 09.B, 11, 16, 19; aislamiento y sincronización pendientes |
| Android Basics 7 | WorkManager para recordatorios configurables de hábitos | 19; programación, fecha y cancelación pendientes |
| Android Basics 8 | Interoperabilidad Views/Compose | No requerida para la UI declarativa; revisar compatibilidad de BiometricPrompt en 15 |

| Tema transversal | Etapa | Evidencia pendiente |
|---|---|---|
| Responsabilidad única de MainActivity | 01 | Inspección de MainActivity y FitTrackApp; compilación previa reportada |
| Actuador háptico | 14 | Vibración real, compatibilidad y cancelación |
| Respuesta única biométrica | 15 | Éxito, errores, cancelación y sesión protegida |
| Flujo continuo de pasos | 16 | callbackFlow, sesión activa, permisos y alternativa manual |
| Backend push | 17 | FCM server, destinatarios, outbox y reintentos |
| FCM Android | 18 | Permisos, canales, recepción y navegación protegida |
| WorkManager | 19 | Trabajo único por usuario y cancelación |
| Seguridad e integración | 20 | Autorización, concurrencia y pruebas locales |
| AWS | 21 | EC2 única, PostgreSQL, HTTPS, presupuesto y respaldo |
| Aceptación final | 22 | E2E, dispositivos, criterios oficiales y evidencias |

La fase 02.2 aporta evidencia local previa de Compose, Material 3, state hoisting, tema oscuro, tipografía, previews y accesibilidad: 3 pruebas unitarias y 11 instrumentadas aprobadas en Pixel 10 Pro XL AVD, render de previews y arranque real verificado antes de los ajustes 02.3. En la revisión 02.3 aprobaron compilación, 3 pruebas unitarias, lint y 11 pruebas instrumentadas actuales en Pixel 10 Pro XL AVD; el arranque tras los ajustes se verificó visualmente. La prueba de contraseña ahora comprueba el texto enmascarado en `EditableText` y los botones deshabilitados reciben una pulsación táctil de prueba. Esta evidencia cubre el sistema de diseño; no demuestra pantallas funcionales ni cumplimiento completo de la rúbrica. La rúbrica oficial de Unidad 4 exige Room con SQLite y consulta offline de un recurso. PostgreSQL sigue siendo la base relacional remota y DataStore conserva sesión y preferencias; la implementación de Room queda planificada y aprobada, pendiente de implementación. No declarar cobertura académica completa sin criterios oficiales del docente y pruebas ejecutadas.

**Etapa 03, evidencia actual:** FastAPI modular, SQLAlchemy 2, Alembic, Argon2id, JWT y Pydantic validados con PostgreSQL 18.6 local. La primera suite sobre `fittrack_test` vacía aprobó 48 pruebas sin omisiones, incluidas migración upgrade/downgrade/upgrade, unicidad concurrente y ambos roles. La repetición tras corregir Alembic y las pruebas de seguridad aprobó 47 con 1 omisión deliberada del segundo downgrade sobre una base ya migrada; no hubo fallos ni advertencia Starlette/httpx. `pip check`, `compileall`, `alembic check` y HTTP local de registro, login y `users/me` satisfactorios. La regresión Android anterior de etapa 03.1 aprobó assembleDebug, 3 pruebas unitarias y lint (0 errores, 12 advertencias); no se modificó Android en esta revisión. La etapa 02 quedó integrada en `develop` mediante PR #1 y merge commit `581bc52523dace75d55b7cedee4c13c05064ef16`. La integración Android de autenticación sigue pendiente para etapa 04. La evidencia técnica redactada está en el directorio local hermano `FitTrack-references/evidence/stage-03/validation.txt`, fuera del repositorio.

## Cobertura oficial propuesta v5.1

La fuente vigente para los nueve criterios es [trazabilidad de rúbrica](rubric-compliance.md), contrastada con `Proyecto_Unidad4_App_MVP.pdf` y `Rubrica_Proyecto_Unidad4.pdf`. Las exclusiones históricas de Room quedan sustituidas: Room con consulta offline es obligatorio. Contactame, tres procesos completos, FCM, tres hardware y pruebas Android específicas siguen pendientes. El backend de etapa 03 fue integrado con PR #2; sus pruebas locales no equivalen a CI ni demuestran cumplimiento integral de la aplicación. La diferencia entre 40 puntos de portada y pesos analíticos de 100 se documenta sin inventar conversión.

## Evidencia local 04.1 · 27-11-2026

Se implementó la parte Android de HU01–HU05/RF-01–RF-05: registro/login contra contrato real, capas domain/data/presentation con Hilt, DTO/mappers, errores tipados, sesión cifrada y navegación por rol. C03/C04/C05/C06/C09 avanzan parcialmente; no alcanzan VERIFIED por este cambio. Room y Contactame siguen en sus etapas.

**Resultados 04.1:** assembleDebug, testDebugUnitTest y lintDebug con código 0; 25 pruebas unitarias aprobadas (22 nuevas y 3 previas), cero fallos; lint sin errores y 22 advertencias de versiones disponibles, etiqueta redundante y recursos previos sin uso. La suite instrumentada completa terminó con código 0; después del ajuste IO se repitieron selectivamente las dos nuevas pruebas, ambas aprobadas en emulator-5554 (Android 17). El aviso sun.misc.Unsafe proviene de protobuf del ejecutor. Un error inicial de lint en el test se corrigió con ViewModelProvider.Factory, sin deshabilitar comprobaciones.

Pruebas unitarias locales: validaciones y normalización sin alterar contraseña, ambos roles, HTTP con MockWebServer (rutas/JSON/Bearer/401/403/409/422), restauración/expiración, logout, respuestas antiguas y fallos de almacenamiento; pruebas de UseCases y ambos ViewModels con fakes. Instrumentación en emulador Android 17: cifrado con nonce distinto, DataStore, invalidación de clave y cambio del grafo tras logout. Estos resultados no son GitHub Actions ni E2E con PostgreSQL.

Fuentes: contrato y esquemas backend leídos directamente; documentos rectores y siete referencias PNG pertinentes consultados. Los PDF docentes siguen externos; no se reextrajo su texto en esta etapa porque pdftotext no está instalado. Se reutilizó la auditoría oficial 03.R; no se atribuye una nueva inspección visual de PDF.

## Validación R1–R2 FitTrack Gym v6.0 · 27-11-2026

Backend: 61 aprobadas (incluida CLI privada de emisión/listado/revocación sin exponer códigos en listados), 0 fallidas, 1 omisión (downgrade global histórico omitido porque fittrack_test ya contiene tablas; se probó el nuevo upgrade/downgrade/upgrade en esquema aislado transaccional de esa misma base, con usuario previo preservado). La fixture verifica base, host local, rol no superusuario y propiedad antes de migrar/pruebas. Alembic check en fittrack_test: sin nuevas operaciones. fittrack_dev permanece en bd7cce2c8602; consulta de solo lectura informa 1 ATHLETE y 1 TRAINER anteriores sin modificar.

Android: assembleDebug, testDebugUnitTest y lintDebug código0; 25 unitarias aprobadas, 0 fallos; lint0 errores/22 advertencias. Incluye DTO sin role/código en memoria, errores invitación/capacidad y regresión de sesión; nueva GymRegisterPreview renderizada. E2E móvil real v6.0, hardware, Room, FCM, Contactame y HTTPS pendientes. Las pruebas instrumentadas históricas de 04.1 no se presentan como nueva ejecución v6.0. Errores iniciales de expectativas antiguas/test se corrigieron; no se deshabilitaron pruebas. Sin CI, commit ni push nuevos.

## Validación R2.1 · 28-11-2026 (PARCIAL)

**Desarrollo:** destino local fittrack_dev y revisión bd7cce2c8602 verificados antes de modificar; head único a600invites01. Respaldo custom pg_dump de 4274 bytes en FitTrack-references/evidence/v6-auth/private/fittrack_dev_20261127_235826.dump, fuera de Git, con SHA-256 cb9de1290e517268180df219d65ef4dc279f8f95f28804ff6f6b80f65c5203df. pg_restore verificó listado y lectura completa del archivo sin restaurar datos. ACL comprobada: solo usuario local y SYSTEM. Upgrade head aplicado; revisión, is_active, tabla, CHECKs, FK, índice y Alembic check satisfactorios. Los dos usuarios previos se compararon antes/después y se conservaron activos exactamente; no hubo downgrade/drop/truncate en desarrollo.

**HTTP real:** Uvicorn actualizado en 127.0.0.1:8000, sin access log; health y OpenAPI200. Registro ATHLETE/TRAINER201 con rol de invitación; código ausente/role extra422; código incorrecto/usado400 INVALID_INVITATION; duplicados409; login/me200; credenciales incorrectas/token ausente/inválido401. Consumo y paso reserva→ocupado comprobados en PostgreSQL. CLI real capacity/legacy-users/invite/list/revoke/deactivate ejercitada sin publicar códigos; una cuenta ficticia desechable nueva se desactivó y perdió acceso con JWT previo. Se conservan las dos cuentas anteriores y una cuenta ficticia activa nueva por rol. Capacidad final ATHLETE:100 límite/2 activos/0 reservas/98 disponibles; TRAINER:10/2/0/8. Invitaciones de prueba sin usar revocadas. No existe prueba403 de negocio porque no hay recurso restringido aún.

**Regresión:** pytest exclusivamente fittrack_test verificado:61 aprobadas/0 fallidas/1 omitida (downgrade global histórico por base ya poblada; migración incremental probada en esquema transaccional aislado). assembleDebug/testDebugUnitTest/lintDebug código0:25 unitarias aprobadas/0 fallos; lint0 errores/22 advertencias (versiones, etiqueta redundante y recursos previos). connectedDebugAndroidTest final código0, XML:13 aprobadas/0 fallidas/0 omitidas. Un primer intento instrumentado fue interrumpido por instalación paralela del APK; se repitió sin interferencia. Aviso sun.misc.Unsafe de protobuf del ejecutor, sin actualizar dependencias ni deshabilitar pruebas.

**Sesión:** revisión puntual confirma AES/GCM/Keystore/DataStore, expiración de servidor, validación /me al restaurar, generaciones contra respuestas antiguas/401 ajeno y bloqueo al fallar almacenamiento. Pruebas unitarias y de Keystore/logout instrumentadas aprobadas; no equivalen a E2E móvil real. Manifiestos/HTTPS release conservados. Única corrección funcional R2.1: texto de sesión expirada cambiado a «Tu sesión ha expirado. Conéctate a Internet e inicia sesión para continuar.».

**Bloqueo UI:** APK debug instalado después de la instrumentación en emulator-5554 (Pixel 10 Pro XL, Android17). Login aparece en jerarquía de accesibilidad, pero la superficie de la app se captura negra y pulsar Crear cuenta mediante ADB y herramienta IDE no cambia la pantalla. Reproducido tras reiniciar solo el proceso y prueba reversible de renderer; causa app/emulador no determinada. Renderer original skiagl y conectividad habilitada al terminar. Las capturas negras son diagnóstico, NO evidencia visual aprobada de Login/Registro. No se insertaron credenciales/códigos en esas capturas. Las cuentas de ambos roles se crearon por HTTP, no por UI.

| Caso E2E real | Estado | Evidencia / acción manual pendiente |
|---|---|---|
| E01 Registro ATHLETE | BLOCKED | No se pudo abrir Registro; resolver superficie negra en emulador/dispositivo antes de rellenar invitación individual |
| E02 Registro TRAINER | NOT_RUN | Crear invitación TRAINER en terminal privada y registrar desde UI; debe abrir Mis rutinas |
| E03 Login y rol | NOT_RUN | Entrar con cada cuenta ficticia y comprobar Hoy/Mis rutinas |
| E04 Invitación inválida/reutilizada | NOT_RUN | En Registro válido en los demás campos, enviar código inválido o consumido; esperar mensaje genérico |
| E05 Confirmación distinta | NOT_RUN | Introducir contraseñas distintas; esperar error y botón deshabilitado |
| E06 Credenciales incorrectas | NOT_RUN | Login con contraseña errónea; esperar error genérico sin acceso privado |
| E07 Duplicados | NOT_RUN | Usar invitación vigente y correo/username de prueba existente; esperar409 y reserva disponible |
| E08 Restauración | NOT_RUN | Tras login, forzar cierre solo de app y abrir; validar /me y mismo rol |
| E09 Red y reintento | NOT_RUN | Con sesión vigente, desconectar red/reabrir, verificar bloqueo privado y reintentar tras reconectar |
| E10 Logout/back stack | NOT_RUN | Cancelar diálogo primero; después confirmar, pulsar Atrás y reabrir sin recuperar privado |
| E11 Logout offline | NOT_RUN | Desde sesión validada, desconectar, salir y reabrir; Login sin recuperar sesión; restaurar red |
| E12 Cambio de cuenta | NOT_RUN | Logout atleta→login entrenador y recorrido inverso sin datos/navegación cruzados |

Para retomar: comprobar visualmente Login en emulador desde Android Studio; si sigue negro, probar otro AVD/dispositivo o reiniciar manualmente ese emulador antes de diagnosticar cambios de código. Instalar app-debug.apk después de finalizar instrumentación, sin instalaciones concurrentes. Mantener FastAPI activo y las direcciones debug existentes. Obtener invitaciones en CLI privada, nunca en argumentos/logs/capturas; capturar formularios vacíos y destinos seguros. No marcar estos casos PASS por los tests con fakes.

Evidencias externas: evidence/v6-auth/diagnostico-login-negro.png y 01-login.png (ambas diagnóstico negro); R2.1-results.json (sin secretos), scripts privados de comprobación y respaldo restringido. Faltan capturas funcionales de registro, roles, error, logout y red. No se acredita validación completa ni listo para versionado. Sin commit ni push.

## R2.2 — Validación de autenticación (2026-11-28)

El usuario reportó como satisfactorios los registros de Carlos (atleta) y Luis (entrenador), destinos Hoy/Mis rutinas, login de ambos roles, restauración de sesión vigente, logout con protección del back stack, logout sin conexión y aislamiento entre cuentas. Es evidencia manual reportada por el usuario, no una ejecución automatizada. R2.1.1 verificó HTTP Android 401/201/200 y confirmó el bloqueo de red local de Android 17 mediante revocación/concesión controlada del permiso debug.

Pendientes: errores E2E restantes, regresión instrumentada del manifiesto final, 403 en recursos de negocio, limpieza futura Room/FCM/hardware, biometría, HTTPS AWS, revisión de cuentas anteriores a invitaciones y seguridad/integración final. La cobertura visual global permanece parcial; las previews nuevas no se consideran inspeccionadas.
