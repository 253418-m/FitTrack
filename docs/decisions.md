# Decisiones y asuntos pendientes · FitTrack v5.0

## Requisitos confirmados por el propietario

MainActivity cumple responsabilidad única: aloja Compose, el tema y FitTrackApp, sin lógica de negocio. Son obligatorias API REST propia, FastAPI, PostgreSQL en la **misma instancia EC2** que la API, presupuesto académico de **50 USD**, dos roles y vínculo formal, CRUD de rutinas, integración de hardware solicitada por el docente y notificaciones push, hábitos locales y calculadora en memoria. Un solo módulo Android inicialmente. No Room, RDS, microservicios, Firebase como sustituto de la API ni aplicaciones separadas por rol. En etapa 01 solo se implementan arranque local y `GET /health`.

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
10. UUID como ID público; formato y normalización de username/correo por definir.
11. Rangos seguros para peso, estatura y edad adulta por definir.
12. Estructura uniforme de errores y expiración JWT por definir.

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
- Advertencia de deprecación **Starlette/httpx** observada en las pruebas previas de TestClient: revisar en etapa 03; no cambiar dependencias durante esta revisión.
- Verificar permisos IAM, región, vigencia, tipos de instancia, precios y saldo antes de crear AWS. `t3.small` es opción, no recurso elegido.
- Una EC2 es punto único de fallo y exige respaldo/restauración externos autorizados. EBS, IPv4, transferencia, snapshots y créditos CPU pueden generar cargos.
- HTTPS exige nombre y certificado confiables; no desactivar validación TLS.
- La etapa 01 se publicó en GitHub con commit `16f05f788efcbd199278d0291d2e90f7b7974693`. La etapa 02.2 permanece local; la revisión 02.3 aprobó compilación, 3 pruebas unitarias, lint y 11 pruebas instrumentadas en Pixel 10 Pro XL AVD. El arranque actual se verificó y la captura final se conserva fuera del repositorio. La prueba de contraseña se ajustó para distinguir `InputText` editable de `EditableText` enmascarado. Quedan pendientes la comprobación en dispositivo físico y TalkBack. La autoría real de la etapa 02 fue confirmada por el propietario como `253699-bot`; la identidad se configuró solo en este repositorio. No se ha realizado push.
- RDS se excluye por requisito y presupuesto; Room se omite porque hábitos son locales y datos relacionales remotos.
