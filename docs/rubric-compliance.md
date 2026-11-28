# Trazabilidad de la rúbrica · FitTrack v5.1 aprobado

**Estado vigente R1–R2 · FitTrack Gym v6.0 (27-11-2026):** alcance aprobado por el usuario y aclaraciones docentes aportadas por él. Sustituye expresamente las reglas incompatibles v5.1 de registro libre/selección de rol, cinco hábitos/meta 3 de 5 y cumplimiento manual de pasos. Las secciones previas incompatibles se conservan como evidencia histórica, sin vigencia normativa. No se atribuye una inspección nueva de material docente. Implementación actual: autenticación e invitaciones; procesos de negocio, Room, FCM, hardware, Contactame y HTTPS siguen pendientes.

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

**Fecha:** 27-11-2026. **Fuentes oficiales:** `Proyecto_Unidad4_App_MVP.pdf` y `Rubrica_Proyecto_Unidad4.pdf`, ubicados en `FitTrack-references/material-docente` fuera del repositorio. Se citan por página y no se publican. Se extrajo el texto de las tres páginas de ambos documentos en la auditoría 03.R; no se verificó su maquetación visual. No se asigna una nota final. La portada de la rúbrica indica **40 puntos** (p. 1), mientras la tabla analítica suma **100 % / 100 puntos** (pp. 2–3); la conversión requiere aclaración docente. La frase sobre ≥15 historias puede interpretarse como total o por proceso; la rúbrica p. 2 dice 15 o más en el documento completo.

**Estados:** VERIFIED = evidencia comprobada del requisito completo; PARTIAL = parte implementada; PLANNED = aceptado en plan sin ejecutar; PROPOSED = decisión aún sin aprobar; MISSING = falta documentación o ejecución; NOT_VERIFIED = no se pudo comprobar. El estado documental y el funcionamiento se reportan por separado. Una prueba local no es CI; una intención de etapa no es implementación.

| Criterio / fuente | Requisito documental | Implementación actual | Prueba ejecutada y evidencia disponible | Pendiente verificable / trazabilidad | Estado documental / funcional |
|---|---|---|---|---|---|
| C01 · 10 % · rúbrica p. 2, proyecto p. 2 | Negocio, roles, 3 procesos, ≥15 HU, criterios, MoSCoW, wireframes Must | Alcance aprobado: RF-01–RF-56, HU01–HU32 y P01–P03; 24 vistas base + Contáctame (25 vistas únicas); 17 PNG v5.1 inspeccionados | Sin prueba nueva; inventario visual previo | P03 aprobado mediante D01; completar brechas de 26 HU Must y cotejar app; diseño base inspeccionado en 03.R.3; [RF](requirements.md), [HU](user-stories.md), [procesos](business-processes.md), [visual](design/mockup-inventory.md) | PARTIAL / MISSING |
| C02 · 15 % · rúbrica p. 2, proyecto p. 2 | Tres procesos completos, API real, roles, carga/vacío/error/reintento | Auth únicamente; P01/P02 planificados, P03 planificado y aprobado | HTTP auth local anterior, sin E2E de procesos | HU08–HU21, HU30–HU32; etapas 05–11 y 20–22; P01–P03 con datos PostgreSQL | PLANNED / MISSING |
| C03 · 10 % · rúbrica p. 2, proyecto p. 2 | Register/Login, ViewModel, sesión, roles, logout, Contáctame/Intent | Backend registro/login/me implementado; Android/contacto pendientes | PostgreSQL/HTTP local etapa 03 | HU01–HU07, HU22; 04/04.B; ViewModel, 401, logout y contacto persistido | PLANNED / PARTIAL |
| C04 · 15 % · rúbrica p. 2, proyecto p. 3 | API HTTPS, DB, JWT, 401/403, JSON, auth/me/contact/devices y CRUD | FastAPI/PostgreSQL/JWT auth implementados; otras 25 rutas futuras documentadas | 48 aprobadas iniciales; 47 aprobadas/1 omitida repetición local; Swagger auth | HU06, HU08–HU19, HU23, HU25, HU27, HU30–HU32; recursos, 403 real, HTTPS y dominio técnico de ambos; 04.B/05/08/11.A/17/21 | PLANNED / PARTIAL |
| C05 · 15 % · rúbrica p. 3, proyecto pp. 1 y 3 | MVVM/Clean/Hilt, UseCase, StateFlow y UiState | Hilt y sistema visual base; arquitectura objetivo documentada | Compilación y pruebas base Android previas, no ViewModel de negocio | HU01–HU32; 04–20; capas reales, fakes y composables sin negocio | PLANNED / PARTIAL |
| C06 · 10 % · rúbrica p. 3, proyecto p. 3 | Retrofit 3, Serialization, OkHttp, DTO/mappers, Room offline y errores | Documentado; aún no implementado | Ninguna prueba Room/Retrofit | HU03/HU04/HU15/HU17; 04/09.B; caché propia, JWT vigente, reconexión y 401 | PLANNED / MISSING |
| C07 · 10 % · rúbrica p. 3, proyecto pp. 2–3 | FCM, device registration, Admin, permiso, canal, destino | Eventos y contratos planificados | Ninguna prueba FCM real | HU23; RF-31–RF-35/RF-38–RF-39/RF-46; 17–18; entrega real en dispositivos | PLANNED / MISSING |
| C08 · 10 % · rúbrica p. 3, proyecto p. 2 | Tres hardware distintos útiles, capas, permisos y liberación | Vibración, biometría y pasos documentados | Ninguna prueba física | HU20–HU22; 14–16; validar equipo físico y aceptación docente de biometría | PLANNED / MISSING |
| C09 · 5 % · rúbrica p. 3, proyecto p. 3 | UseCases/ViewModels con fakes, Git ambos, README URL, Catalog | Version Catalog y contribuciones históricas; URL pública pendiente | Pruebas base Android previas y backend local; no sustituyen pruebas requeridas | HU Must y criterios; 20–22; pruebas ViewModel/UseCase, PR reales, URL HTTPS | PLANNED / PARTIAL |

## Estrategia de pruebas vinculada a las Must

- **Backend:** unitarias de reglas; PostgreSQL real para restricciones, propiedad, transacciones, concurrencia, migraciones y HTTP 401/403/409/422; fakes de Firebase Admin y después prueba FCM real. `fittrack_test` aislada; no SQLite como sustituto del servidor.
- **Android:** UseCases y al menos un ViewModel con fakes; validaciones, UiState y cancelación; Retrofit/MockWebServer, interceptor, 401/403, DataStore/Keystore, logout/back stack; Room DAO, aislamiento, offline, reconexión y revocación; Intents, notificaciones, permisos y hardware presente/ausente.
- **Aceptación:** P01–P03 completos con API real, ambos roles y dos dispositivos cuando el intercambio lo requiera; pruebas en dispositivo físico para hardware, push real y URL HTTPS pública. Capturar carga, vacío, error y reintento. Registrar resultado por HU y criterio, no convertir pruebas planificadas en aprobadas.
- **Evidencia histórica:** etapa 02, 3 unitarias Android y 11 instrumentadas; etapa 03, primera suite backend 48 aprobadas, repetición 47 aprobadas/1 omitida por downgrade ya realizado, 0 fallidas; pip/compileall/Alembic y HTTP local satisfactorios. No se ejecutaron pruebas durante esta actualización documental. No hay estados CI de GitHub registrados para PR #2.

**Riesgos:** alcance P03/contacto/caché aprobado; quedan validación de hardware físico, firewall PostgreSQL, protección contra abuso y despliegue HTTPS faltan. No declarar LOGRADO sin implementación y evidencia verificable.

**Revisión 03.R.2.1:** se verificaron por separado IDs, referencias, enlaces y formato; no se ejecutaron pruebas de aplicación ni se renderizaron diagramas Mermaid. La matriz visual de 26 HU Must fue cotejada en 03.R.3; quedan estados y wireframes obligatorios pendientes. Los PDF docentes se citan por nombre/página y no se incorporan al repositorio.

## Evidencia visual 03.R.3 (sin implementación)

Se abrieron los 13 PNG v5.1 y los 24 PNG de vistas principales originales, dos variantes y la lámina de notificaciones. Se localizaron 13 SVG homónimos, no renderizados individualmente. El [inventario visual](design/mockup-inventory.md) distingue **25 vistas únicas** (24 originales y Contáctame), 12 referencias nuevas de variantes/láminas, aceptación general de dirección visual y cobertura **parcial** de 26 HU Must. HU32 consulta/copia de identificación pública es Must; HU28 comparte mediante Intent como Could. HU18 incluye restablecimiento diario con confirmación, pero no existe aún wireframe del diálogo. Ninguna referencia demuestra API, Room, hardware físico, FCM ni pruebas de aceptación. C01 documental mejora en trazabilidad; C02–C09 no cambian a VERIFIED por esta inspección.

## Complemento 03.R.3.1

Se registraron 03.E (error de guardado), 03.F (409), 03.G (confirmación de restablecimiento) y 19.B (configuración entrenador con ajustes), inspeccionando solo sus PNG. Total: 17 PNG/17 SVG localizados, 25 vistas únicas, 56 RF, 32 HU (26 Must, 5 Should, 1 Could). C01 mejora evidencia visual de HU18 y configuración del entrenador; cobertura global parcial. C02–C09 no cambian de estado por disponer de imágenes. Permanecen pendientes expiración offline, permiso/sensor, estados biométricos y permiso/destino FCM; contacto y confirmaciones pueden reutilizar componentes, sujetos a cotejo y pruebas. Ver [inventario actualizado](design/mockup-inventory.md). Ninguna decisión de alcance pendiente queda aprobada.

## Evidencia incremental 04.1 · 27-11-2026

Android incorpora registro/login, UseCases, ViewModels Hilt, Retrofit/Serialization/OkHttp, sesión cifrada DataStore/Keystore y navegación protegida por rol. C03, C04, C05, C06 y C09 siguen PARTIAL funcionalmente: aún faltan E2E real 04.2, Contactame, Room, pruebas finales, HTTPS y demás recursos de negocio. No se acredita HU22, hardware físico ni aprobación docente.

Las pruebas de esta entrega son locales: fakes, MockWebServer y emulador; no se declaran CI. El [plan](development-plan.md) y [cobertura](course-coverage.md) registran alcance y resultados. Se mantienen íntegros D01–D11, la cobertura visual global parcial y verificaciones externas pendientes.

## Alineación v6.0 R1–R2

C01: requisitos/historias/procesos actualizados, visuales v6.0 parciales; sin LOGRADO global. C02: P01 selección/vínculo, P02 rutinas diarias y P03 cumplimiento/seguimiento planificados; no E2E de negocio. C03/C04: registro invitado, rol servidor, cuenta activa y CLI implementados; validación E2E móvil real pendiente. C05/C06: arquitectura Android/red/sesión reutilizada, Room pendiente. C07/C08: FCM/hardware pendientes; pasos sin alternativa manual, biometría con validación docente pendiente. C09: pruebas locales de regresión y concurrencia; sin CI nueva, commit ni autoría atribuida.

La actualización conserva los nueve criterios y sus pesos. No se repite auditoría oficial ni se declara cumplimiento funcional por documentos/previews. Brechas y evidencia histórica v5.1 permanecen diferenciadas.

## R2.2 — Validación de autenticación (2026-11-28)

El usuario reportó como satisfactorios los registros de Carlos (atleta) y Luis (entrenador), destinos Hoy/Mis rutinas, login de ambos roles, restauración de sesión vigente, logout con protección del back stack, logout sin conexión y aislamiento entre cuentas. Es evidencia manual reportada por el usuario, no una ejecución automatizada. R2.1.1 verificó HTTP Android 401/201/200 y confirmó el bloqueo de red local de Android 17 mediante revocación/concesión controlada del permiso debug.

Pendientes: errores E2E restantes, regresión instrumentada del manifiesto final, 403 en recursos de negocio, limpieza futura Room/FCM/hardware, biometría, HTTPS AWS, revisión de cuentas anteriores a invitaciones y seguridad/integración final. La cobertura visual global permanece parcial; las previews nuevas no se consideran inspeccionadas.
