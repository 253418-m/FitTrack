# Despliegue AWS previsto para la etapa 21

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

**Estado actual: NO DESPLEGADO.** En etapa 01 no se consultaron credenciales ni se crearon recursos AWS. Laboratorio académico universitario: presupuesto total de **50 USD**. Ningún precio actual se considera conocido. El tipo de instancia está por seleccionar; `t3.small` es solo una opción pendiente de restricciones, rendimiento y costo.

## Topología prevista

Una única instancia Amazon EC2 con Ubuntu Server y un volumen EBS persistente para sistema y datos PostgreSQL. En ella correrán FastAPI mediante Uvicorn como servicio Systemd, PostgreSQL local y Nginx como proxy inverso. Android accederá por HTTPS a Nginx; Nginx enviará tráfico al Uvicorn en localhost; FastAPI conectará a PostgreSQL en localhost. No Amazon RDS, Aurora, segunda instancia de base de datos, balanceador, NAT Gateway adicional, ECS, EKS o infraestructura distribuida.

La instancia única reduce complejidad y posibles gastos, pero es un punto único de fallo. EBS persiste según configuración; se debe comprobar qué ocurre al terminar la instancia y cuándo expira el laboratorio.

## Comprobaciones previas a cualquier aprovisionamiento

Confirmar región autorizada, permisos IAM, vigencia y saldo del laboratorio, tipos de instancia y arquitectura permitidos, disponibilidad de memoria/CPU, límites de red y precio real de la configuración. Evaluar `t3.small` y alternativa menor solo con mediciones locales de FastAPI + PostgreSQL. Estimar cómputo EC2, EBS, IPv4 pública, transferencias, copias, snapshots, posibles créditos CPU y recursos accesorios. Mantener margen presupuestario para pruebas y presentación. Detener EC2 cuando sea compatible con disponibilidad requerida; EBS/IP/snapshots pueden seguir cobrando. Ninguna creación de recursos está autorizada por esta documentación.

## Red, TLS y seguridad

Security Group previsto: 443 público para HTTPS; 80 solo si se necesita para redirección o certificados; 22 restringido a orígenes SSH autorizados. No abrir 5432 ni el puerto interno de Uvicorn a Internet. PostgreSQL escuchará solo en localhost; Uvicorn también en localhost o interfaz privada. Verificar las reglas efectivas, no solo la intención escrita.

Usar certificado TLS válido y confiable y nombre DNS adecuado; disponibilidad y costo pendientes. No comprar dominio/certificado sin autorización. Android no desactivará validación TLS ni aceptará certificados autofirmados indiscriminadamente. HTTP solo local en debug.

Usuario de sistema no root para FastAPI; usuario PostgreSQL exclusivo y de permisos mínimos para FitTrack, nunca superusuario habitual. JWT, `DATABASE_URL` y credenciales en variables/archivos protegidos fuera de Git, sin logs sensibles ni exposición por Nginx. Mantener actualizaciones de seguridad. No incluir credenciales AWS ni claves SSH privadas en Android o repositorio.

## Datos, respaldo y recuperación

Migraciones Alembic versionadas y revisadas; nunca `create_all` permanente ni migraciones destructivas sin revisión/autorización. Respaldo con `pg_dump` u opción equivalente. Definir destino externo a la instancia, cifrado, retención, permisos y costo autorizados; no guardar dumps en Git. Probar una restauración en entorno aislado y documentar pasos y tiempo de recuperación. Exportar datos autorizados antes de que termine el laboratorio si los recursos pueden desaparecer.

## Secuencia prevista, aún no ejecutada

1. Superar etapas locales 03–20 y sus pruebas de autenticación, vínculos, rutinas, hardware, seguridad, backend FCM, recepción Android y procesamiento del outbox.
2. Obtener autorización explícita para recursos/costos tras verificar laboratorio, precios y presupuesto.
3. Aprovisionar una sola EC2 Ubuntu y EBS con red restringida.
4. Instalar Python y PostgreSQL, crear venv, base y usuario restringido; cargar configuración protegida.
5. Aplicar migraciones Alembic revisadas.
6. Configurar Systemd/Uvicorn y Nginx; validar configuración antes de reiniciar.
7. Configurar HTTPS confiable y BASE_URL de producción sin IP hardcodeada.
8. Probar respaldo y restauración autorizados; registrar costos y operación/detención.

## Aceptación de la etapa 21

Verificar servicio y conexión PostgreSQL, migraciones, Systemd, configuración Nginx, TLS, `GET /health` y operaciones de autenticación/solicitudes/vínculos/CRUD desde Android. Validar además backend FCM, recepción Android y procesamiento del outbox con eventos autorizados, reintentos e idempotencia. `GET /health` solo mide el proceso; no demuestra disponibilidad de PostgreSQL. Verificar que 5432 y Uvicorn no sean accesibles públicamente, persistencia real entre dispositivos, reinicio controlado, recuperación de respaldo y saldo restante. No hacer pruebas destructivas ni de carga excesiva en producción. Si no se consigue HTTPS válido o respaldo seguro, reportar bloqueo; no declarar despliegue completo.

## Servicios excluidos

Amazon RDS, Aurora, ECS, EKS, Lambda como reemplazo de FastAPI, API Gateway innecesario, balanceador, Auto Scaling, instancias GPU y servicios adicionales sin necesidad/permiso. No hay dominio, IP, identificador de instancia, certificado, precio ni recurso AWS asignado en etapa 01.

## Notificaciones futuras

Firebase Admin SDK y un procesador sencillo del outbox se añadirán durante la etapa 17. Credenciales privadas fuera de Git; verificar salida HTTPS a FCM, reintentos e idempotencia. No usar Redis, Celery, RabbitMQ ni RDS. Sin recursos creados durante esta revisión.

## Requisitos de seguridad para el alcance v5.1 propuesto

La URL HTTPS pública sigue **pendiente**. Mantener EC2 Ubuntu, Nginx → Uvicorn/FastAPI vía localhost, PostgreSQL local restringido, systemd y certificado TLS válido; sin RDS. Restringir SSH y 5432 a orígenes autorizados, verificar firewall y `pg_hba.conf` antes de afirmar aislamiento externo. El servidor PostgreSQL local previo escuchaba en varias interfaces; escucha no demuestra exposición a Internet. No se cambian servicios en esta etapa.

Guardar JWT, base de datos y credenciales Firebase Admin fuera de Git; separar entorno y copias de seguridad, probar restauración. Antes de publicar: control de intentos repetidos de login y Contactame, autorización 401/403 en recursos reales, HTTPS, monitoreo y presupuesto académico de 50 USD. Ningún recurso AWS/Firebase ni dominio fue creado durante 03.R.2.
