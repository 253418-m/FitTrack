# Requisitos de FitTrack · alcance v5.1 aprobado

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

Estado: contrato histórico v5.0 actualizado con propuesta v5.1. La etapa 03, integrada en develop por PR #2, implementó en código el componente backend de RF-01 y RF-02 (registro, login, JWT y usuario actual); su validación backend contra PostgreSQL real y HTTP local aprobó. La integración Android sigue pendiente. RF-03 a RF-05 requieren la etapa Android siguiente, y los demás requisitos siguen planificados. Dos roles registrados libremente: `ATHLETE` y `TRAINER`. Un entrenador admite muchos atletas; cada atleta, como máximo un entrenador activo. Toda autorización remota corresponde a la API.

| ID | Requisito |
|---|---|
| RF-01 | Registro con nombre personal obligatorio, username y correo únicos, contraseña confirmada y rol inmutable; unicidad de username y correo en PostgreSQL. |
| RF-02 | Login común por correo y contraseña; JWT e identidad con rol; errores comprensibles. |
| RF-03 | Sesión en Preferences DataStore, token protegido por Android Keystore y validación al abrir; desbloqueo biométrico opcional sin reemplazar login remoto JWT. |
| RF-04 | Logout borra sesión y estado privado, desvincula instalaciones, cancela recordatorios y seguimiento de hardware, limpia caché Room y back stack; el historial remoto permanece bajo la cuenta. |
| RF-05 | Navegación protegida por rol: atleta a Hoy, entrenador a Rutinas; backend es autoridad final. |
| RF-06 | Calculadora compartida: sexo de fórmula, actividad, peso, estatura y edad con controles adecuados. |
| RF-07 | IMC y categoría, TMB Mifflin-St Jeor y mantenimiento en tiempo real; cálculo local puro, sin diagnóstico. |
| RF-08 | Cinco hábitos fijos con ID estable, checklist, progreso y pasos observados opcionalmente por sensor; persistencia remota propuesta para v5.1, con alternativa manual. |
| RF-09 | Progreso y meta diaria de tres de cinco hábitos son Must; racha actual/mejor e historial ampliado son Should. Fecha local inyectable y datos remotos confirmados. |
| RF-10 | Atleta consulta solo rutinas asignadas mediante vínculo activo; carga, vacío, error y reintento. |
| RF-11 | Detalle de rutina del atleta de solo lectura con entrenador y ejercicios ordenados. |
| RF-12 | Entrenador consulta solo rutinas propias, atleta asignado y estado del vínculo. |
| RF-13 | Detalle de rutina propia del entrenador con edición y eliminación. |
| RF-14 | Crear rutina para un atleta activo, con nombre, grupo, nivel, duración y uno o más ejercicios; transacción. |
| RF-15 | Editar rutina propia y ejercicios, reasignando solo a atleta activo; fecha original preservada. |
| RF-16 | Eliminar rutina propia con confirmación es Must; restaurar mediante Snackbar Deshacer es Should y requiere política de eliminación lógica aprobada. |
| RF-17 | Mis atletas muestra solo vínculos activos, identidad pública y conteo de rutinas del vínculo. |
| RF-18 | Retroalimentación y errores de todas las operaciones; éxito solo tras confirmación de API. |
| RF-19 | Atleta sin entrenador busca entrenadores por username o ID; debounce, cancelación y estados. |
| RF-20 | Perfil público básico del entrenador y estado de relación, sin datos privados. |
| RF-21 | Atleta envía solicitud `PENDING`; no se crea vínculo ni permisos de rutina. |
| RF-22 | Entrenador ve solo solicitudes recibidas con datos básicos y acciones. |
| RF-23 | Aceptar solicitud propia pendiente crea vínculo activo en la misma transacción; unicidad bajo concurrencia. |
| RF-24 | Rechazar cambia a `REJECTED` sin crear vínculo. |
| RF-25 | Atleta consulta estado `PENDING/ACCEPTED/REJECTED/CANCELLED`; cancelar pendiente es propuesta MVP. |
| RF-26 | Mi entrenador muestra identidad y fecha de vínculo o estados sin vínculo/solicitud. |
| RF-27 | Atleta termina vínculo con confirmación; registra actor/fecha y revoca acceso. |
| RF-28 | Entrenador termina vínculo de atleta propio; impide nuevas asignaciones, conserva usuarios. |
| RF-29 | Detalle de atleta vinculado: identidad, inicio, rutinas y acciones; sin hábitos o métricas. |
| RF-30 | Entrenador consulta y copia su username o UUID público completo para habilitar la búsqueda (Must); compartirlo mediante Intent es Could. |
| RF-31 | Registrar o actualizar instalación autenticada y token FCM de forma segura. |
| RF-32 | Push de nueva solicitud al entrenador. |
| RF-33 | Push de solicitud aceptada al atleta. |
| RF-34 | Push de solicitud rechazada al atleta. |
| RF-35 | Push de rutina asignada al atleta. |
| RF-36 | Recordatorio diario local de hábitos pendientes mediante WorkManager. |
| RF-37 | Preferencias de notificaciones por usuario, horario y activación. |
| RF-38 | Navegación protegida desde notificaciones. |
| RF-39 | Permisos y canales de notificaciones según API Android. |
| RF-40 | Retroalimentación háptica ante eventos reales. |
| RF-41 | Configuración opcional de biometría. |
| RF-42 | Desbloqueo biométrico local sin sustituir JWT. |
| RF-43 | Seguimiento de pasos con TYPE_STEP_COUNTER durante sesión activa. |
| RF-44 | Progreso hacia meta de pasos, separado del contador acumulado del dispositivo. |
| RF-45 | Disponibilidad, permisos y alternativas ante hardware ausente. |
| RF-46 | Desvincular instalaciones y cancelar trabajos/seguimiento al cerrar sesión. |

RF-31 a RF-46 son requisitos del propietario planificados, sin implementación. Los detalles como horario inicial 20:00, seguimiento por sesiones, almacenamiento de pasos y patrón de outbox son propuestas técnicas pendientes de validar. SRP de MainActivity es requisito no funcional confirmado por el propietario.

## Reglas y validaciones

Las solicitudes transitan solo de `PENDING` a `ACCEPTED`, `REJECTED` o `CANCELLED`. El vínculo es entidad independiente e histórica; la solicitud aceptada no cambia al desvincular. PostgreSQL debe impedir dos vínculos activos por atleta. La API debe comprobar rol, propiedad, vigencia y transiciones en cada operación, también bajo concurrencia. No se confía en IDs del cliente para determinar propietario. Rutinas: exactamente un atleta asignado, al menos un ejercicio, enteros positivos y ejercicios ordenados. Las reglas propuestas sobre solicitudes pendientes, acceso a rutinas históricas y eliminación se registran en [decisions.md](decisions.md).

La calculadora utilizará IMC = kg / m²; TMB hombre = 10kg + 6.25cm - 5edad + 5, mujer = 10kg + 6.25cm - 5edad - 161; mantenimiento = TMB × factor 1.2/1.375/1.55/1.725. Categorías IMC: <18.5, <25, <30 y ≥30; clasificar sin redondear. Entradas iniciales: hombre, moderada, 70 kg, 172 cm, 25 años. Rangos de adultos pendientes de definir antes de la etapa 12.

Hábitos fijos: ocho vasos de agua, 8,000 pasos, siete horas de sueño, 30 minutos de entrenamiento y 10 de estiramiento. Se registran manualmente, salvo la integración opcional del sensor durante una sesión activa para observar pasos; el hábito de caminar 8,000 pasos conserva alternativa manual. No se promete seguimiento continuo de 24 horas. Si hoy aún no cumple y ayer sí, la racha previa continúa hasta terminar el día. Conservar historial suficiente para mejor racha y cruzar meses/años.

## Calidad, infraestructura y exclusiones

UI en español, Material 3 oscuro, accesibilidad (contraste 4.5:1, táctiles 48 dp, semántica, texto escalable), estados completos, pruebas unitarias, de red, UI, backend PostgreSQL y extremo a extremo. API REST propia, HTTPS en producción, datos sensibles fuera de Git, errores tipados y sin trazas internas. Una sola EC2 con PostgreSQL y FastAPI, presupuesto del laboratorio 50 USD; no RDS.

La exclusión anterior de Room/SQLite Android y consulta offline quedó sustituida: ambos son obligatorios según la rúbrica oficial de Unidad 4. Fuera del alcance actual permanecen Health Connect, GPS, cámara, micrófono, Bluetooth, NFC, chat, pagos, cambio de rol, panel admin, seguimiento corporal por entrenador, varios entrenadores activos, infraestructura distribuida y Firebase como sustituto de API. Hardware elegido, FCM como transporte y WorkManager para recordatorios sí son requisitos planificados.

## Actualización v5.1 propuesta · ingeniería de requerimientos

**Fecha:** 27-11-2026. Esta propuesta prioriza la [especificación y rúbrica oficiales](rubric-compliance.md) y conserva los 46 IDs anteriores; únicamente `users` y el backend de RF-01/02 están implementados. La etapa 03 se integró en `develop` mediante [PR #2](https://github.com/253418-m/FitTrack/pull/2), merge `12df7874276bece8e682a5540806238f2145fff1`. Los cambios de alcance posteriores requieren aprobación antes de implementarse.

**Negocio:** atletas necesitan rutinas personalizadas y progreso saludable; entrenadores necesitan administrar vínculos, atletas y rutinas. El MVP aporta P01 vínculo formal, P02 rutinas asignadas y P03 hábitos/actividad **propuesto** con datos de API; [flujos](business-processes.md), [historias](user-stories.md), [modelo](data-model.md) y [contrato](api-contract.md). La app web es opcional.

### Actores y matriz de permisos objetivo

| Funcionalidad | Público | ATHLETE | TRAINER | Restricción servidor |
|---|---|---|---|---|
| Registro/login | Sí | Sí | Sí | Rol elegido al registro, inmutable |
| Sesión, logout, identidad y Contáctame | Solo Login/registro; Contactame público no aprobado | Sí | Sí | JWT para recursos privados; 401 sin identidad |
| Búsqueda/perfil de entrenador | No | Sí | No | Perfil sin email, hash ni atletas |
| Crear/cancelar solicitud | No | Sí | No | Solo solicitud propia, sin vínculo incompatible |
| Recibir/aceptar/rechazar solicitud | No | No | Sí | Solo destinatario; 403 para rol ajeno |
| Consultar/terminar vínculo | No | Propio | Propio | Participante autorizado; máximo uno activo por atleta |
| Consultar atletas vinculados | No | No | Sí | Solo vínculos propios |
| Crear/editar/eliminar rutina | No | No | Sí | Entrenador propietario y vínculo vigente |
| Leer rutina y caché | No | Asignada | Propia | API revalida vínculo/propiedad; caché por usuario |
| Registrar/consultar hábitos | No | Propios | No | Identidad del JWT, nunca user_id del cliente |
| Push/dispositivo | No | Propio | Propio | Instalación asociada al usuario autenticado |
| Calculadora, preferencias, biometría | No | Propio | Propio | Datos locales segregados; biometría no sustituye JWT |

`401` significa autenticación ausente/inválida; `403` significa identidad válida sin permiso. Ocultar botones no sustituye autorización API. La UI debe presentar carga, vacío, error y reintento.

### RF incorporados — propuestos, sin implementación

| ID | Requisito | Origen / proceso |
|---|---|---|
| RF-47 | Contáctame envía formulario validado a `POST /api/v1/contact` y persiste mensaje real; campos y acceso final pendientes. | Docente, transversal |
| RF-48 | Contáctame abre al menos un Intent de contacto con dato autorizado y maneja ausencia de aplicación; correo/teléfono/WhatsApp según disponibilidad real. | Docente |
| RF-49 | Room guarda copia local de rutinas asignadas y ejercicios autorizados, con esquema versionado. | Docente, P02 |
| RF-50 | Atleta consulta copia Room vigente sin conexión; distinguir sin caché de lista vacía y mostrar antigüedad. | Docente, P02 |
| RF-51 | Al recuperar conectividad durante flujo activo, revalidar API y actualizar/eliminar copias; reintento manual y errores tipados. | Docente, P02 |
| RF-52 | Cache privada aislada por usuario y limpiada al logout; no mostrar ante 401/revocación expresa; política offline limitada pendiente. | Seguridad, P02 |
| RF-53 | Propuesta P03: API persiste por atleta/fecha el estado de cinco hábitos, progreso observado acotado y auditoría UTC, con unicidad real. | Negocio propuesto |
| RF-54 | Propuesta P03: API devuelve día, historial suficiente y rachas derivadas; meta al completar al menos 3 de 5. | Negocio propuesto |
| RF-55 | Propuesta P03: atleta corrige hábitos con versión optimista y puede restablecer el registro diario propio tras confirmación explícita; conflicto concurrente 409 sin sobrescritura silenciosa. DELETE físico aprobado D04: comparar UUID de instancia no reutilizable y versión, incluso al recrear. | Negocio propuesto |
| RF-56 | Formularios, red y persistencia presentan errores tipados y reintento; nunca muestran éxito antes de confirmación remota. | Docente, transversal |

**Inventario propuesto: 56 RF únicos.** RF-08/09 pasan de persistencia exclusivamente DataStore a remota **solo si se aprueba P03**; se mantienen sus cinco hábitos, meta y semántica de racha. RF-04 pasa a limpiar también caché Room. RF-10/11 incorporan lectura offline condicionada. RF-31–35 y RF-40–45 permanecen planificados. No se reutiliza ningún ID con significado incompatible.

### Priorización MoSCoW revisada y cobertura

| Prioridad | Alcance verificable |
|---|---|
| MUST | HU01–HU23, HU30–HU32: auth, roles, Contactame, P01–P03 propuesto, CRUD de rutinas, progreso diario, Room, FCM y los tres hardware. RF-08, RF-09 (meta básica), RF-12, RF-16 (eliminar), RF-26 y RF-30 (consulta/copia del identificador público) se mantienen explícitos. También HTTPS, Clean/Hilt/Retrofit y pruebas de UseCase/ViewModel. |
| SHOULD | HU24–HU27 y HU29: calculadora, rachas ampliadas, recordatorio local, Deshacer y preferencias avanzadas. RF-09 (racha), RF-16 (Deshacer), RF-36 y RF-37 se separan de la funcionalidad Must correspondiente. |
| COULD | HU28: copiar/compartir username o UUID público mediante Intent; estadísticas adicionales e historial independiente de entrenamientos. |
| WONT en este MVP | Web opcional, chat, pagos, múltiples entrenadores activos, escritura offline, seguimiento avanzado de repeticiones, RDS, Redis, microservicios y Firebase Authentication. |

Las preferencias o permisos **indispensables** para recibir FCM y asociar/desasociar dispositivo son Must dentro de HU23/RF-31–RF-35, RF-38–RF-39 y RF-46; solo la personalización avanzada es Should. La activación biométrica es voluntaria para el usuario aunque HU22 se planifica como Must para demostrar el tercer hardware. No se rebajan compromisos ya acordados sin aprobación. P03 remoto sigue propuesto.

### Trazabilidad RF → HU (cobertura semántica planificada)

| RF | HU responsable | Proceso / observación |
|---|---|---|
| RF-01–RF-02 | HU01–HU02 | Auth; backend probado, Android pendiente |
| RF-03–RF-05 | HU03–HU05, HU22 | Sesión, logout, rol |
| RF-06–RF-07 | HU24 | Calculadora Should |
| RF-08–RF-09 | HU18–HU20, HU25–HU26 | P03; meta Must, rachas y recordatorio Should |
| RF-10–RF-11 | HU15, HU17 | P02; lectura autorizada/offline |
| RF-12–RF-13 | HU16, HU30 | P02; rutinas propias entrenador |
| RF-14–RF-16 | HU14, HU16, HU27 | P02; crear/editar/borrar Must, Deshacer Should |
| RF-17–RF-18 | HU13–HU16, HU30 | P02; atletas y estados de operación |
| RF-19–RF-20 | HU08 | P01; búsqueda/perfil público |
| RF-21–RF-24 | HU09–HU10 | P01; solicitud y resolución |
| RF-25–RF-26 | HU11, HU31 | P01; estado/entrenador activo |
| RF-27–RF-29 | HU12–HU13 | P01/P02; fin y detalle de atleta |
| RF-30 | HU08, HU32, HU28 | Búsqueda y copia Must; compartir Intent Could |
| RF-31–RF-35 | HU23 | FCM y dispositivos Must |
| RF-36–RF-37 | HU26, HU29 | Recordatorio/preferencias Should |
| RF-38–RF-39 | HU23 | Destino protegido, permiso/canal Must |
| RF-40–RF-42 | HU21–HU22 | Háptica y biometría Must |
| RF-43–RF-45 | HU20–HU22 | Pasos, disponibilidad y alternativas Must |
| RF-46 | HU04, HU23 | Logout y desasociación |
| RF-47–RF-48 | HU06–HU07 | Contactame Must |
| RF-49–RF-52 | HU04, HU17 | Room y seguridad offline Must |
| RF-53–RF-55 | HU18–HU19, HU25 | P03 remoto propuesto |
| RF-56 | HU06, HU14–HU19 | Errores y reintentos transversales |

**56 RF únicos en la matriz de requisitos; 32 HU propuestas.** La tabla indica planificación, no funcionamiento verificado. El único componente completo en backend sigue siendo auth etapa 03. Ver [contrato](api-contract.md), [procesos](business-processes.md), [HU](user-stories.md), [rúbrica](rubric-compliance.md).

**No funcionales:** Kotlin/Compose/Material 3, Navigation Compose, MVVM con dominio sin Android, StateFlow/UiState inmutable, corrutinas, Hilt, Retrofit/Serialization/OkHttp, Room y DataStore; accesibilidad, HTTPS, secretos fuera de Git, pruebas reales y Version Catalog. Proyecto de Cálculo Vectorial no aplica.

**Dependencias y aprobaciones:** 03.R.3 crea wireframes; 04 Android auth; 04.B Contactame; 05–10 P01/P02; 09.B Room; 11.A/B P03; luego FCM, hardware, HTTPS y aceptación. Aprobar P03, Contactame, política offline, riesgo biométrico y prioridades Should antes de implementar.
