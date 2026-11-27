# FitTrack

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](docs/decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

Aplicación Android nativa en español y API REST propia para hábitos saludables, calculadora corporal y rutinas entre entrenadores y atletas. La etapa 01 está completada y publicada. La etapa 02 de diseño se completó e integró en `develop` mediante el PR #1 (merge commit `581bc52523dace75d55b7cedee4c13c05064ef16`). La etapa 03 implementó y validó localmente el backend de registro, login y usuario actual sobre PostgreSQL 18.6; la migración `users` y las pruebas de integración pasaron. La etapa quedó versionada e integrada en `develop` mediante PR #2, merge `12df7874276bece8e682a5540806238f2145fff1`. Android conserva solo la pantalla temporal.

## Organización y alcance

- `app/`: Android Kotlin, Compose, Material 3 y base de Hilt/KSP. Un módulo Gradle.
- `backend/`: FastAPI con autenticación, usuario, PostgreSQL y Alembic validados localmente.
- `docs/`: requisitos y arquitectura objetivo. Sus endpoints, entidades y vistas son propuestas hasta que se implementen y prueben.

Ambos roles, `ATHLETE` y `TRAINER`, compartirán la aplicación, la API y PostgreSQL. Android solo accederá a los datos remotos por HTTPS a través de la API. La calculadora funcionará en memoria. La propuesta v5.1 traslada el seguimiento de hábitos a la API/PostgreSQL y exige Room para rutinas offline; DataStore conserva preferencias y datos auxiliares. Este alcance fue aprobado por D01–D03. RDS y servicios distribuidos siguen fuera del alcance.

## Entorno Android

Windows con Android Studio, JDK compatible con AGP 9.5.2 y SDK 37. Se reutiliza Gradle Wrapper 9.6.0. Abrir la raíz en Android Studio y sincronizar Gradle. Para validar en PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug --console=plain
.\gradlew.bat :app:testDebugUnitTest --console=plain
.\gradlew.bat :app:lintDebug --console=plain
```

MainActivity aloja FitTrackApp, que muestra únicamente la identidad temporal de FitTrack. El tema oscuro y los componentes base de la etapa 02.2 aprobaron compilación, lint, pruebas unitarias e instrumentadas y arranque en la revisión 02.2 previa; tras los ajustes 02.3 aprobaron compilación, 3 pruebas unitarias, lint y 11 pruebas instrumentadas; el arranque actual se verificó en Pixel 10 Pro XL AVD; la navegación funcional y las pantallas por rol corresponden a etapas posteriores.

## Entorno backend

Consultar [backend/README.md](backend/README.md) para configurar bases y roles locales exclusivos, variables protegidas, entorno virtual, Alembic, FastAPI y Pytest. `GET /health` solo indica disponibilidad del proceso. El componente backend de RF-01 y RF-02 se validó contra PostgreSQL local; la autenticación y sesión Android corresponden a la etapa 04.

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

## Contexto histórico v5.0 (sustituido por la propuesta v5.1)

Se planificaban 46 requisitos funcionales, 24 referencias de diseño y 22 etapas principales distribuidas entre 253418-m y 253699-bot. Son obligatorios el contador de pasos por SensorManager, biometría opcional por AndroidX Biometric, vibración háptica, push FCM con FastAPI/PostgreSQL como autoridad, y recordatorios locales WorkManager. Las funciones de hardware y notificaciones aún no están implementadas. Ver [requisitos](docs/requirements.md), [arquitectura](docs/architecture.md) y [plan](docs/development-plan.md).

## Propuesta de alcance v5.1 (27 de noviembre de 2026)

Las etapas 01, 02 y 03 están integradas en `develop`; la etapa 03 cerró con el [PR #2](https://github.com/253418-m/FitTrack/pull/2) y merge `12df7874276bece8e682a5540806238f2145fff1`. Esta rama documenta una **alcance aprobado por el usuario** de 56 RF y 32 HU, no una implementación adicional. La etapa 04 no ha comenzado.

La rúbrica de Unidad 4 exige tres procesos de negocio completos, Contactame, Room con acceso sin conexión, FCM, tres tipos de hardware y pruebas Android de casos de uso y ViewModels. [Procesos](docs/business-processes.md), [historias](docs/user-stories.md), [trazabilidad](docs/rubric-compliance.md) y [plan](docs/development-plan.md) son las fuentes rectoras. P03 de hábitos remotos, los campos de Contactame y la política de acceso offline están aprobados por D01–D05. La API HTTPS pública aún no existe.
