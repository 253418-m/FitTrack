# FitTrack

Aplicación Android nativa en español y API REST propia para hábitos saludables, calculadora corporal y rutinas entre entrenadores y atletas. La etapa 01 está completada y publicada. La fase 02.1 de mockups está completada y la fase 02.2 del sistema de diseño tuvo validación local previa. La revisión 02.3 aprobó 11 pruebas instrumentadas actuales y verificó el arranque tras ajustes puntuales; la publicación e integración Git están pendientes. Solo existe una pantalla Android temporal y el endpoint técnico `GET /health`; las funciones de negocio aún no están implementadas.

## Organización y alcance

- `app/`: Android Kotlin, Compose, Material 3 y base de Hilt/KSP. Un módulo Gradle.
- `backend/`: FastAPI local con prueba del endpoint de salud.
- `docs/`: requisitos y arquitectura objetivo. Sus endpoints, entidades y vistas son propuestas hasta que se implementen y prueben.

Ambos roles, `ATHLETE` y `TRAINER`, compartirán la aplicación, la API y PostgreSQL. Android solo accederá a los datos remotos por HTTPS a través de la API. Los hábitos serán locales mediante DataStore; la calculadora funcionará en memoria. No se utilizarán Room, RDS ni servicios distribuidos.

## Entorno Android

Windows con Android Studio, JDK compatible con AGP 9.5.2 y SDK 37. Se reutiliza Gradle Wrapper 9.6.0. Abrir la raíz en Android Studio y sincronizar Gradle. Para validar en PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug --console=plain
.\gradlew.bat :app:testDebugUnitTest --console=plain
.\gradlew.bat :app:lintDebug --console=plain
```

MainActivity aloja FitTrackApp, que muestra únicamente la identidad temporal de FitTrack. El tema oscuro y los componentes base de la etapa 02.2 aprobaron compilación, lint, pruebas unitarias e instrumentadas y arranque en la revisión 02.2 previa; tras los ajustes 02.3 aprobaron compilación, 3 pruebas unitarias, lint y 11 pruebas instrumentadas; el arranque actual se verificó en Pixel 10 Pro XL AVD; la navegación funcional y las pantallas por rol corresponden a etapas posteriores.

## Entorno backend

Consultar [backend/README.md](backend/README.md) para crear el entorno virtual, instalar dependencias, ejecutar FastAPI y correr Pytest. `GET /health` solo indica disponibilidad del proceso.

## Despliegue futuro

La etapa 21 prevé una sola instancia Amazon EC2 con Ubuntu, FastAPI/Uvicorn, PostgreSQL, Nginx y Systemd, sobre EBS y con HTTPS. El laboratorio tiene un presupuesto académico total de 50 USD. No se ha aprovisionado infraestructura; tipo de instancia, costos, permisos y restricciones siguen pendientes. [Plan AWS](docs/aws-deployment.md).

## Documentación

- [Requisitos](docs/requirements.md)
- [Arquitectura](docs/architecture.md)
- [Modelo de datos](docs/data-model.md)
- [Contrato API](docs/api-contract.md)
- [Inventario de mockups](docs/design/mockup-inventory.md)
- [Sistema de diseño](docs/design/design-system.md)
- [Plan de desarrollo](docs/development-plan.md)
- [Cobertura académica](docs/course-coverage.md)
- [Decisiones y pendientes](docs/decisions.md)
- [Integración de hardware planificada](docs/hardware-integration.md)
- [Notificaciones planificadas](docs/notifications.md)
- [Despliegue AWS previsto](docs/aws-deployment.md)

El PDF aprobado, las exportaciones SVG/PNG y el material docente están en el directorio local hermano `FitTrack-references` y permanecen fuera del repositorio. Su inspección efectiva se registra en la documentación de diseño.

## Contexto rector v5.0

Se planifican 46 requisitos funcionales, 24 referencias de diseño y 22 etapas equilibradas entre 253418-m y 253699-bot. Son obligatorios el contador de pasos por SensorManager, biometría opcional por AndroidX Biometric, vibración háptica, push FCM con FastAPI/PostgreSQL como autoridad, y recordatorios locales WorkManager. Estas funciones aún no están implementadas. Ver [requisitos](docs/requirements.md), [arquitectura](docs/architecture.md) y [plan](docs/development-plan.md).
