# Cobertura académica FitTrack v5.0

**Evidencia existente de etapa 01:** MainActivity delega la UI a FitTrackApp (SRP); la revisión previa reportó compilación Android, una prueba unitaria, lint, dos pruebas backend de health, pip check y compileall satisfactorios. Esa evidencia no demuestra funcionalidades futuras ni cumplimiento de rúbrica. Registrar archivos, pruebas y resultados reales conforme se ejecuten las etapas.

| Fuente | Aplicación prevista | Etapa y evidencia pendiente |
|---|---|---|
| Evaluación I | Calculadora local: Compose, ViewModel, StateFlow, funciones puras y sliders | 12; fórmulas y UI |
| Evaluación II | Hábitos fijos, DataStore, Flow, rachas, pasos por sesiones y recordatorios | 11, 16, 19; fechas, aislamiento, sensor y trabajos |
| Evaluación III | API REST, JWT, PostgreSQL, Hilt/Retrofit, vínculos, CRUD y notificaciones remotas | 03–10, 13, 17–18, 20–22; backend, red, UI, seguridad y E2E |
| Android Basics 1 | Kotlin, recursos, estructura Android, Compose y SRP de MainActivity | 01; evidencia local previa de compilación y código |
| Android Basics 2 | Estado, formularios, validaciones y pruebas de lógica | 04, 10, 12; pruebas pendientes |
| Android Basics 3 | Colecciones, LazyColumn, Material y accesibilidad | 02: tema, componentes, tipografía y previews implementados; listas y pantallas completas pendientes en 06–12 |
| Android Basics 4 | ViewModel, Lifecycle, StateFlow, navegación tipada y UDF | 04–12, 18; pruebas de navegación/estado pendientes |
| Android Basics 5 | Corrutinas, Retrofit, JSON, repositorios, errores y Hilt | 04–10, 17–18; MockWebServer e integración pendientes |
| Android Basics 6 | Preferences DataStore; integridad relacional en PostgreSQL backend | 03–04, 11, 16, 19; almacenamiento y aislamiento pendientes |
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

La fase 02.2 aporta evidencia local previa de Compose, Material 3, state hoisting, tema oscuro, tipografía, previews y accesibilidad: 3 pruebas unitarias y 11 instrumentadas aprobadas en Pixel 10 Pro XL AVD, render de previews y arranque real verificado antes de los ajustes 02.3. En la revisión 02.3 aprobaron compilación, 3 pruebas unitarias, lint y 11 pruebas instrumentadas actuales en Pixel 10 Pro XL AVD; el arranque tras los ajustes se verificó visualmente. La prueba de contraseña ahora comprueba el texto enmascarado en `EditableText` y los botones deshabilitados reciben una pulsación táctil de prueba. Esta evidencia cubre el sistema de diseño; no demuestra pantallas funcionales ni cumplimiento completo de la rúbrica. Room/SQLite Android no son requisito; PostgreSQL es la base relacional remota y DataStore almacena datos locales. No declarar cobertura académica completa sin criterios oficiales del docente y pruebas ejecutadas.
