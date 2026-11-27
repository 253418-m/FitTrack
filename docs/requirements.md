# Requisitos del MVP (planificados)

Estado: contrato rector de FitTrack v5.0. La etapa 03 implementó en código el componente backend de RF-01 y RF-02 (registro, login, JWT y usuario actual); su validación backend contra PostgreSQL real y HTTP local aprobó. La integración Android sigue pendiente. RF-03 a RF-05 requieren la etapa Android siguiente, y los demás requisitos siguen planificados. Dos roles registrados libremente: `ATHLETE` y `TRAINER`. Un entrenador admite muchos atletas; cada atleta, como máximo un entrenador activo. Toda autorización remota corresponde a la API.

| ID | Requisito |
|---|---|
| RF-01 | Registro con nombre personal obligatorio, username y correo únicos, contraseña confirmada y rol inmutable; unicidad de username y correo en PostgreSQL. |
| RF-02 | Login común por correo y contraseña; JWT e identidad con rol; errores comprensibles. |
| RF-03 | Sesión en Preferences DataStore, token protegido por Android Keystore y validación al abrir; desbloqueo biométrico opcional sin reemplazar login remoto JWT. |
| RF-04 | Logout borra sesión y estado privado, desvincula instalaciones, cancela recordatorios y seguimiento de hardware, y limpia back stack; conserva hábitos locales según política por usuario. |
| RF-05 | Navegación protegida por rol: atleta a Hoy, entrenador a Rutinas; backend es autoridad final. |
| RF-06 | Calculadora compartida: sexo de fórmula, actividad, peso, estatura y edad con controles adecuados. |
| RF-07 | IMC y categoría, TMB Mifflin-St Jeor y mantenimiento en tiempo real; cálculo local puro, sin diagnóstico. |
| RF-08 | Cinco hábitos fijos con ID estable, checklist, progreso y pasos observados opcionalmente por sensor; DataStore por usuario y fecha, con alternativa manual. |
| RF-09 | Racha actual y mejor, últimos siete días y progreso; día cumplido con al menos tres hábitos; fecha inyectable y recordatorios locales configurables. |
| RF-10 | Atleta consulta solo rutinas asignadas mediante vínculo activo; carga, vacío, error y reintento. |
| RF-11 | Detalle de rutina del atleta de solo lectura con entrenador y ejercicios ordenados. |
| RF-12 | Entrenador consulta solo rutinas propias, atleta asignado y estado del vínculo. |
| RF-13 | Detalle de rutina propia del entrenador con edición y eliminación. |
| RF-14 | Crear rutina para un atleta activo, con nombre, grupo, nivel, duración y uno o más ejercicios; transacción. |
| RF-15 | Editar rutina propia y ejercicios, reasignando solo a atleta activo; fecha original preservada. |
| RF-16 | Eliminar con confirmación y Snackbar Deshacer; eliminación lógica/restauración propuesta por validar. |
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
| RF-30 | Entrenador consulta y copia username e ID público. |
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

La exclusión anterior de Room/SQLite Android y consulta offline queda pendiente de sustitución: ambos son obligatorios según la rúbrica oficial de Unidad 4. Fuera del alcance actual permanecen Health Connect, GPS, cámara, micrófono, Bluetooth, NFC, chat, pagos, cambio de rol, panel admin, hábitos remotos, seguimiento corporal por entrenador, varios entrenadores activos, infraestructura distribuida y Firebase como sustituto de API. Hardware elegido, FCM como transporte y WorkManager para recordatorios sí son requisitos planificados.
