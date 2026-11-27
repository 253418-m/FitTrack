# Arquitectura propuesta

## Estado real

Un módulo Android `app` con Compose y pantalla de arranque; Hilt/KSP configurados. MainActivity únicamente aloja FitTrackApp bajo FitTrackTheme. El tema único y sus tokens están en `ui/theme`; los componentes reutilizables y previews en `core/designsystem`. El sistema de diseño recibe estado y callbacks, sin dependencias hacia features. Backend FastAPI independiente con `GET /health` y módulos `core`, `db`, `auth`, `users` para la etapa 03. Existen modelo, repositorio y servicios de autenticación, además de Alembic; la migración `users` y la integración PostgreSQL fueron validadas localmente en bases aisladas. La etapa 03 aún no está publicada. No existen ViewModels Android de negocio ni sesión Android.

## Android, etapas futuras

MVVM y Clean Architecture ligera, dentro del módulo `app`, organizada por `core` y `features/auth|calculator|habits|coaching|routines`. Crear solo capas necesarias. Presentation: Route obtiene ViewModel por Hilt y recolecta `StateFlow` con ciclo de vida; Screen recibe estado/callbacks. Domain: modelos, contratos y reglas sin Android, Compose ni DTO. Data: Retrofit/DTO/mappers, DataStore y repositorios. Dependencias de presentation y data apuntan a contratos domain; domain no depende de implementaciones. `MutableStateFlow` privado, eventos transitorios separados cuando aplique.

Navigation Compose de rutas tipadas: grafos Auth, Athlete y Trainer, una Activity, RootViewModel y back stack protegido. La calculadora se comparte, sin capa data. `viewModelScope`, funciones `suspend`, cancelación cooperativa y manejo tipado de errores. Retrofit 3, OkHttp y kotlinx.serialization se añadirán cuando exista comunicación remota; AuthInterceptor no leerá DataStore con `runBlocking`. Hilt enlazará contratos e implementaciones cuando existan.

Fuentes de verdad: API/PostgreSQL para usuarios, solicitudes, vínculos y rutinas; Preferences DataStore por usuario/fecha para hábitos y para metadatos de sesión (token protegido con Keystore); memoria de ViewModel para calculadora. Esta exclusión de Room es histórica: la rúbrica oficial de Unidad 4 exige caché local consultable sin conexión; su diseño se abordará en una operación posterior, sin cambios en el backend de etapa 03.

## Backend actual y futuro

FastAPI es un monolito modular. `main.py` compone routers y errores; `core` concentra configuración, Argon2id, JWT y errores; `db` crea engine y sesión por solicitud; `auth` separa router, schemas, servicio y dependencias; `users` separa modelo, repositorio, schema público y router. Los endpoints síncronos de DB/hash se ejecutan como `def` mediante el threadpool de FastAPI. Alembic versiona `users` con revisión `bd7cce2c8602`. La configuración se carga del entorno sin secreto predeterminado; SQLAlchemy oculta parámetros en errores. `PublicUser` contiene email para identidad autenticada y no debe reutilizarse como perfil público de entrenador. `coaching` y `routines` permanecen planificados y no tienen módulos vacíos.

## Despliegue futuro

```text
Android --HTTPS--> Nginx / EC2 --localhost--> Uvicorn / FastAPI --localhost--> PostgreSQL
                        |                  |
                        +------ EBS -------+
```

Una instancia EC2 con Ubuntu, Nginx y Systemd. No se ha desplegado. Puerto 443 previsto; 80 solo redirección/certificados; 22 restringido. Uvicorn y PostgreSQL sin exposición pública. Ver [aws-deployment.md](aws-deployment.md).

## FitTrack v5.0: arquitectura objetivo planificada

MainActivity aloja Compose y delega a FitTrackApp; FitTrackApplication conserva Hilt. RootViewModel y AppNavHost se crearán cuando la autenticación/navegación los necesiten. Las Routes obtendrán ViewModels por Hilt y las Screens recibirán estado/callbacks. MainActivity no contiene lógica de negocio, permisos, hardware ni Firebase.

Hardware previsto: contador TYPE_STEP_COUNTER en habits con SensorManager y callbackFlow/trySend/awaitClose; listener solo durante seguimiento activo, ACTIVITY_RECOGNITION desde API 29, DataStore por usuario/fecha, alternativa manual y manejo de reinicio/cambio de fecha. Biometría opcional con AndroidX Biometric, Keystore y operación suspend cancelable asociada al ciclo de vida; JWT remoto sigue siendo obligatorio. Háptica con VibratorManager API 31+, Vibrator API 26–30, VibrationEffect y cancelación try/finally. Contratos/casos de uso de dominio puro e implementaciones hardware en data; Hilt liga interfaces. Paquetes se crearán al implementar.

Notificaciones: FCM solo transporta eventos remotos; FastAPI/PostgreSQL conservan autoridad. Los eventos autorizados TRAINER_REQUEST_RECEIVED/ACCEPTED/REJECTED y ROUTINE_ASSIGNED se registrarán en outbox dentro de la transacción y se procesarán tras commit, con reintentos/idempotencia. Android registrará instalación autenticada, verificará usuario/permisos al recibir/abrir y desvinculará al logout. WorkManager enviará recordatorios locales de hábitos pendientes, trabajo único por usuario, horario inicial propuesto 20:00 local configurable, sin hora exacta garantizada. POST_NOTIFICATIONS API 33+, canales API 26+. Sin Firebase Authentication ni base de datos Firebase.
