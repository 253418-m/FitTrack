# Historias de usuario · FitTrack v5.1 aprobado

**Estado vigente R1–R2 · FitTrack Gym v6.0 (27-11-2026):** alcance aprobado por el usuario y aclaraciones docentes aportadas por él. Sustituye expresamente las reglas incompatibles v5.1 de registro libre/selección de rol, cinco hábitos/meta 3 de 5 y cumplimiento manual de pasos. Las secciones previas incompatibles se conservan como evidencia histórica, sin vigencia normativa. No se atribuye una inspección nueva de material docente. Implementación actual: autenticación e invitaciones; procesos de negocio, Room, FCM, hardware, Contactame y HTTPS siguen pendientes.

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

**Fecha:** 27-11-2026. **Estado:** propuesta para revisión; no equivale a funcionalidades terminadas. La especificación oficial (p. 2) vincula historias con cada proceso y la rúbrica (p. 2) exige **15 o más historias en el documento**. Se proponen 32; queda por aclarar con el docente la posible lectura de 15 por proceso. [Procesos](business-processes.md) · [RF](requirements.md) · [Trazabilidad](rubric-compliance.md). Los IDs y prioridades no autorizan implementación hasta aprobar el alcance P03 y políticas pendientes.

**Convenciones:** las rutas abreviadas se anteponen a `/api/v1`; Para P03 se propone PUT completo con versión y DELETE explícito; PATCH no está propuesto simultáneamente. `MUST` significa necesario para la meta académica; la activación biométrica por usuario sigue siendo voluntaria. Los wireframes de historias Must se mapean en [inventario visual](design/mockup-inventory.md); una variante descrita aún no es un wireframe aprobado. Los criterios expresan resultado verificable; cada etapa desarrollará casos detallados.

## HU01 · Registro de dos roles

- **Actor / proceso / prioridad / RF:** Visitante; Transversal; MUST; RF-01.
- **Historia:** Como visitante, quiero crear una cuenta como atleta o entrenador para acceder a las funciones de mi rol.
- **Precondiciones:** sin cuenta.
- **Criterio de aceptación:** Dado un nombre, username, correo y contraseña válidos con confirmación coincidente y rol ATHLETE o TRAINER, cuando envío Registro, entonces la API persiste una sola cuenta y devuelve 201 con JWT y usuario sin hash. Dado username o correo ya utilizado, devuelve 409; ante datos inválidos, 422 sin exponer contraseñas.
- **Flujo satisfactorio:** Registro → sesión. **Alternativos:** Duplicado/422.
- **Pantallas / API:** Registro; POST /auth/register.
- **Pruebas previstas / etapa / estado:** API, ViewModel, PostgreSQL; 04; PARTIAL (backend local implementado; Android pendiente).

## HU02 · Inicio de sesión

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; MUST; RF-02.
- **Historia:** Como usuario registrado, quiero iniciar sesión con mi correo y contraseña para continuar mis actividades privadas.
- **Precondiciones:** cuenta vigente.
- **Criterio de aceptación:** Dada una cuenta vigente, cuando envío correo normalizado y contraseña correcta, entonces recibo 200 con JWT y rol real. Si la contraseña es incorrecta o el correo no existe, recibo el mismo mensaje genérico 401 sin datos de la cuenta.
- **Flujo satisfactorio:** Login → destino del rol. **Alternativos:** 401/422/red.
- **Pantallas / API:** Login; POST /auth/login.
- **Pruebas previstas / etapa / estado:** API, ViewModel, interceptor; 04; PARTIAL (backend local implementado; Android pendiente).

## HU03 · Sesión persistente

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; MUST; RF-03.
- **Historia:** Como usuario autenticado, quiero recuperar mi sesión al abrir la app para continuar donde estaba sin repetir el login mientras siga vigente.
- **Precondiciones:** login válido.
- **Criterio de aceptación:** Dado un token guardado detrás de una abstracción protegida y aún vigente, cuando reinicio la app, entonces valida identidad y abre el destino del rol. Si expiró o la API devuelve 401, elimina acceso privado y solicita login; biometría no renueva el JWT.
- **Flujo satisfactorio:** Reapertura → identidad. **Alternativos:** Token expirado/almacenamiento fallido.
- **Pantallas / API:** Desbloqueo/Inicio; GET /users/me.
- **Pruebas previstas / etapa / estado:** UseCase, ViewModel, DataStore; 04; PLANNED.

## HU04 · Cerrar sesión

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; MUST; RF-04,RF-46,RF-52.
- **Historia:** Como usuario autenticado, quiero cerrar sesión para impedir que otra persona vea mis datos en este dispositivo.
- **Precondiciones:** sesión activa.
- **Criterio de aceptación:** Dada una sesión activa, cuando pulso Cerrar sesión, entonces se desasocia el dispositivo, se eliminan token, preferencias de sesión y caché Room privada, se cancelan trabajos y se limpia el back stack. Volver atrás no muestra datos privados.
- **Flujo satisfactorio:** Cerrar → Login. **Alternativos:** Sin red: reconciliación de desvinculación pendiente.
- **Pantallas / API:** Ajustes/Login; DELETE /devices/{installation_id}.
- **Pruebas previstas / etapa / estado:** UseCase, navegación, Room; 04/09.B/18; PLANNED.

## HU05 · Destinos por rol

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; MUST; RF-05.
- **Historia:** Como atleta o entrenador, quiero entrar a mi área correspondiente para acceder solo a las acciones permitidas a mi rol.
- **Precondiciones:** sesión autenticada.
- **Criterio de aceptación:** Dado un JWT válido, cuando la API resuelve mi rol, entonces ATHLETE abre Hoy y TRAINER abre Rutinas. Un usuario de rol distinto que solicita un recurso restringido recibe 403 aunque manipule la navegación local.
- **Flujo satisfactorio:** Inicio por rol. **Alternativos:** 401/403.
- **Pantallas / API:** Hoy/Rutinas; GET /users/me.
- **Pruebas previstas / etapa / estado:** Navegación, API, ViewModel; 04; PLANNED.

## HU06 · Formulario Contáctame

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; MUST; RF-47,RF-56.
- **Historia:** Como usuario autenticado, quiero enviar una consulta desde Contáctame para dejar constancia de mi mensaje al equipo de FitTrack.
- **Precondiciones:** sesión vigente; política propuesta.
- **Criterio de aceptación:** Dada mi identidad autenticada y asunto/mensaje válidos, cuando envío el formulario, entonces la API guarda el mensaje asociado a mi cuenta y devuelve 201 tras confirmar PostgreSQL. La pantalla muestra carga y éxito; 401, 422, 429 o fallo de red muestran error y permiten reintento sin fingir envío.
- **Flujo satisfactorio:** Enviar → confirmación API. **Alternativos:** 422/429/503.
- **Pantallas / API:** Contáctame; POST /contact.
- **Pruebas previstas / etapa / estado:** ViewModel, API, PostgreSQL; 04.B; PLANNED.

## HU07 · Intents de contacto

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; MUST; RF-48.
- **Historia:** Como usuario, quiero abrir un canal de contacto autorizado desde Ajustes para comunicarme con FitTrack mediante una aplicación instalada.
- **Precondiciones:** datos de contacto autorizados disponibles.
- **Criterio de aceptación:** Dada una dirección de contacto real y autorizada, cuando elijo Correo, entonces ACTION_SENDTO abre una aplicación compatible. Si no existe aplicación o dato autorizado, se informa la limitación sin fallo ni dirección inventada.
- **Flujo satisfactorio:** Elegir canal → Intent. **Alternativos:** Sin aplicación/valor no configurado.
- **Pantallas / API:** Contáctame; Ninguno.
- **Pruebas previstas / etapa / estado:** Instrumentada de Intent; 04.B; PLANNED.

## HU08 · Buscar entrenador

- **Actor / proceso / prioridad / RF:** Atleta; P01; MUST; RF-19,RF-20.
- **Historia:** Como atleta, quiero buscar entrenadores por username o UUID y ver su perfil público para elegir a quién solicitar asesoría.
- **Precondiciones:** sin vínculo incompatible.
- **Criterio de aceptación:** Dado un atleta autenticado sin restricción incompatible, cuando busca un username o UUID, entonces ve coincidencias y perfil con nombre/username autorizados, sin email ni atletas. Una búsqueda vacía muestra estado vacío y un rol no autorizado recibe 403.
- **Flujo satisfactorio:** Buscar → perfil. **Alternativos:** Vacío/404/red/401.
- **Pantallas / API:** Buscar/Perfil; GET /trainers, GET /trainers/{id}.
- **Pruebas previstas / etapa / estado:** API, ViewModel; 07; PLANNED.

## HU09 · Solicitar vínculo

- **Actor / proceso / prioridad / RF:** Atleta; P01; MUST; RF-21.
- **Historia:** Como atleta sin entrenador activo, quiero enviar una solicitud a un entrenador para iniciar una relación que permita rutinas personalizadas.
- **Precondiciones:** entrenador válido; sin vínculo.
- **Criterio de aceptación:** Dado un entrenador existente y sin vínculo o solicitud incompatible, cuando envío la solicitud, entonces queda PENDING en PostgreSQL y aún no puedo leer sus rutinas. Duplicado, vínculo activo o carrera concurrente devuelven 409 sin crear dos solicitudes válidas.
- **Flujo satisfactorio:** Enviar → PENDING. **Alternativos:** 409/403/red.
- **Pantallas / API:** Perfil/Estado; POST /coaching/requests.
- **Pruebas previstas / etapa / estado:** API, concurrencia, ViewModel; 05/07; PLANNED.

## HU10 · Resolver solicitud

- **Actor / proceso / prioridad / RF:** Entrenador; P01; MUST; RF-22,RF-23,RF-24.
- **Historia:** Como entrenador, quiero consultar y aceptar o rechazar solicitudes recibidas para decidir qué atletas podré asesorar.
- **Precondiciones:** solicitud propia PENDING.
- **Criterio de aceptación:** Dada una solicitud PENDING dirigida a mí, cuando la acepto, entonces la API crea un único vínculo activo en la misma transacción y confirma el estado; cuando la rechazo, termina sin vínculo. Solicitud ajena devuelve 403/404 y una transición repetida 409.
- **Flujo satisfactorio:** Ver → aceptar/rechazar. **Alternativos:** Conflicto concurrente/403.
- **Pantallas / API:** Solicitudes; GET /coaching/requests/received, POST /coaching/requests/{id}/accept|reject.
- **Pruebas previstas / etapa / estado:** PostgreSQL, concurrencia, UI; 05/06; PLANNED.

## HU11 · Cancelar solicitud

- **Actor / proceso / prioridad / RF:** Atleta; P01; MUST; RF-25.
- **Historia:** Como atleta, quiero consultar el estado y cancelar una solicitud pendiente para corregir una petición que ya no deseo mantener.
- **Precondiciones:** solicitud propia PENDING.
- **Criterio de aceptación:** Dada una solicitud PENDING propia, cuando la consulto, entonces veo su estado; cuando la cancelo, entonces queda CANCELLED sin vínculo. Una solicitud ajena no se revela y una solicitud ya resuelta no vuelve a PENDING.
- **Flujo satisfactorio:** Cancelar → estado. **Alternativos:** 403/409/red.
- **Pantallas / API:** Estado solicitud; POST /coaching/requests/{id}/cancel.
- **Pruebas previstas / etapa / estado:** API, ViewModel; 05/07; PLANNED.

## HU12 · Terminar vínculo

- **Actor / proceso / prioridad / RF:** Participante; P01; MUST; RF-27,RF-28.
- **Historia:** Como atleta o entrenador vinculado, quiero terminar mi relación activa para detener futuras asignaciones y accesos asociados.
- **Precondiciones:** vínculo activo propio.
- **Criterio de aceptación:** Dado un vínculo activo del que soy participante, cuando confirmo finalizarlo, entonces se registra actor y fecha, se bloquean nuevas asignaciones y la API revoca la lectura remota de rutinas del vínculo. Un tercero recibe 403/404; la caché se revalida al reconectar.
- **Flujo satisfactorio:** Confirmar → fin. **Alternativos:** 403/409/red.
- **Pantallas / API:** Mi entrenador/Detalle atleta; POST /coaching/relationships/{id}/end.
- **Pruebas previstas / etapa / estado:** API, autorización, UI; 05/06/07; PLANNED.

## HU13 · Consultar mis atletas

- **Actor / proceso / prioridad / RF:** Entrenador; P02; MUST; RF-17,RF-29.
- **Historia:** Como entrenador, quiero consultar mis atletas activos para elegir correctamente a quién asignar una rutina.
- **Precondiciones:** sesión TRAINER.
- **Criterio de aceptación:** Dado un entrenador autenticado, cuando abre Mis atletas, entonces ve solo vínculos propios activos y puede elegir un destinatario; sin vínculos ve estado vacío. Un atleta no puede consultar esa lista y recibe 403.
- **Flujo satisfactorio:** Abrir → lista. **Alternativos:** Vacío/401/503.
- **Pantallas / API:** Mis atletas/Detalle; GET /coaching/athletes*.
- **Pruebas previstas / etapa / estado:** API, ViewModel; 06; PLANNED.

## HU14 · Crear y asignar rutina

- **Actor / proceso / prioridad / RF:** Entrenador; P02; MUST; RF-14,RF-18.
- **Historia:** Como entrenador, quiero crear y asignar una rutina con ejercicios a un atleta vinculado para organizar su entrenamiento.
- **Precondiciones:** atleta vinculado.
- **Criterio de aceptación:** Dado un vínculo activo propio y al menos un ejercicio válido, cuando guardo, entonces rutina y ejercicios se confirman en una transacción y se crea el evento ROUTINE_ASSIGNED; solo después aparece éxito. Vínculo terminado o datos inválidos rechazan sin rutina parcial.
- **Flujo satisfactorio:** Crear → detalle. **Alternativos:** 422/403/409/red.
- **Pantallas / API:** Formulario rutina; POST /routines.
- **Pruebas previstas / etapa / estado:** Transacción, API, ViewModel; 08/10; PLANNED.

## HU15 · Consultar rutina asignada

- **Actor / proceso / prioridad / RF:** Atleta; P02; MUST; RF-10,RF-11.
- **Historia:** Como atleta, quiero ver mis rutinas asignadas y sus ejercicios para realizar el entrenamiento indicado por mi entrenador.
- **Precondiciones:** vínculo activo.
- **Criterio de aceptación:** Dado un vínculo activo y rutinas autorizadas, cuando abro lista o detalle, entonces la API muestra solo mis asignaciones y ejercicios ordenados; lista sin rutinas es vacía, no error. Un atleta distinto recibe 403/404 sin datos ajenos.
- **Flujo satisfactorio:** Abrir lista → detalle. **Alternativos:** Vacío/403/404.
- **Pantallas / API:** Mis rutinas/Detalle; GET /routines, GET /routines/{id}.
- **Pruebas previstas / etapa / estado:** API, UI, autorización; 09; PLANNED.

## HU16 · Editar o eliminar rutina

- **Actor / proceso / prioridad / RF:** Entrenador; P02; MUST; RF-13,RF-15,RF-16.
- **Historia:** Como entrenador, quiero editar o eliminar mis rutinas para mantener actualizadas las asignaciones de mis atletas.
- **Precondiciones:** rutina propia.
- **Criterio de aceptación:** Dada una rutina propia, cuando edito datos válidos, entonces se actualizan sin cambiar propietario; cuando confirmo eliminar, deja de aparecer para el atleta y la API responde según contrato. Rutina ajena devuelve 403/404; restaurar con Deshacer pertenece a HU27 Should.
- **Flujo satisfactorio:** Editar/eliminar → confirmación. **Alternativos:** 422/403/409.
- **Pantallas / API:** Detalle/Formulario; PUT /routines/{id}, DELETE /routines/{id}.
- **Pruebas previstas / etapa / estado:** API, ViewModel; 08/10; PLANNED.

## HU17 · Rutinas sin conexión

- **Actor / proceso / prioridad / RF:** Atleta; P02; MUST; RF-49,RF-50,RF-51,RF-52.
- **Historia:** Como atleta, quiero consultar una copia autorizada de mis rutinas sin conexión para seguir el plan durante una interrupción de red.
- **Precondiciones:** copia autorizada y vigente.
- **Criterio de aceptación:** Dada sesión local protegida, JWT vigente y copia Room autorizada para mi cuenta, cuando falla la red, entonces veo lista/detalle con última sincronización y aviso offline. Sin copia veo sin caché, no lista vacía; 401 o sesión expirada bloquean acceso, y al reconectar se revalida y reemplaza la copia.
- **Flujo satisfactorio:** Abrir offline → recuperar red. **Alternativos:** 401/revocación/caché caducada.
- **Pantallas / API:** Mis rutinas/Detalle; GET /routines*.
- **Pruebas previstas / etapa / estado:** DAO, RepositoryImpl, UI offline; 09.B; PLANNED.

## HU18 · Registrar y corregir hábitos

- **Actor / proceso / prioridad / RF:** Atleta; P03; MUST; RF-08,RF-53,RF-55,RF-56.
- **Historia:** Como atleta, quiero registrar y corregir los hábitos de mi día para que mi progreso confirmado refleje lo que hice.
- **Precondiciones:** sesión ATHLETE; P03 aprobado por D01.
- **Criterio de aceptación:** Dada sesión ATHLETE y fecha local válida, cuando marco o corrijo un hábito, entonces la API confirma el registro propio y la UI muestra la versión guardada. Cuando elijo Restablecer progreso, la UI solicita confirmación y solo tras DELETE propio confirmado elimina el registro del día y recalcula progreso/racha. Si cancelo, nada cambia. Un conflicto de versión devuelve 409 y exige recargar; fallo de red no se presenta como guardado. D04 aprueba eliminación física con control de identidad de instancia y versión.
- **Flujo satisfactorio:** Marcar o corregir → confirmación; Restablecer → confirmar → DELETE → día sin registro. **Alternativos:** cancelar restablecimiento, 401/409/422/503.
- **Pantallas / API:** Hoy y diálogo de restablecimiento; PUT y DELETE /habits/daily/{date}.
- **Pruebas previstas / etapa / estado:** API, concurrencia, DELETE/confirmación/cancelación y ViewModel; 11.A/11.B; PLANNED.

## HU19 · Ver progreso e historial

- **Actor / proceso / prioridad / RF:** Atleta; P03; MUST; RF-09,RF-54.
- **Historia:** Como atleta, quiero consultar mi progreso diario e historial para saber si alcancé la meta de tres hábitos.
- **Precondiciones:** registros del atleta.
- **Criterio de aceptación:** Dado un día sin registro, cuando consulto, entonces recibo un estado vacío válido; con registro veo los cinco estados confirmados y meta alcanzada si al menos tres son verdaderos. Un entrenador no accede; las rachas ampliadas se cubren en HU25 Should.
- **Flujo satisfactorio:** Abrir Hoy → historial. **Alternativos:** Vacío/cambio fecha/red.
- **Pantallas / API:** Hoy; GET /habits/daily/{date}, GET /habits/history.
- **Pruebas previstas / etapa / estado:** Fechas, API, ViewModel; 11.A/11.B; PLANNED.

## HU20 · Observar pasos

- **Actor / proceso / prioridad / RF:** Atleta; P03; MUST; RF-43,RF-44,RF-45.
- **Historia:** Como atleta, quiero observar mis pasos mientras uso el seguimiento para conocer mi avance físico sin confundirlo con un conteo continuo.
- **Precondiciones:** sesión de seguimiento.
- **Criterio de aceptación:** Dado sensor disponible y permiso aplicable concedido, cuando inicio seguimiento, entonces se muestra progreso observado desde baseline del usuario/fecha y se libera listener al salir. Reinicio o cambio de cuenta no duplica pasos; sensor o permiso ausente ofrece entrada manual y Ajustes cuando procede.
- **Flujo satisfactorio:** Iniciar → detener. **Alternativos:** Sensor ausente/permiso denegado.
- **Pantallas / API:** Seguimiento pasos; PUT /habits/daily/{date}.
- **Pruebas previstas / etapa / estado:** Fake sensor, dispositivo físico; 16; PLANNED.

## HU21 · Confirmación háptica

- **Actor / proceso / prioridad / RF:** Atleta; P03; MUST; RF-40,RF-45.
- **Historia:** Como atleta, quiero una confirmación háptica al guardar mi meta diaria para reconocer que el avance quedó registrado.
- **Precondiciones:** meta recién confirmada por API.
- **Criterio de aceptación:** Dado que la API confirmó por primera vez la meta de tres hábitos, cuando llega esa confirmación al ViewModel, entonces vibra una sola vez. Reintento, recomposición o respuesta fallida no duplican vibración; sin motor la operación de hábitos sigue funcionando.
- **Flujo satisfactorio:** Meta → vibración. **Alternativos:** Motor ausente/cancelación.
- **Pantallas / API:** Hoy; Respuesta de hábitos.
- **Pruebas previstas / etapa / estado:** UseCase fake, dispositivo físico; 14; PLANNED.

## HU22 · Desbloqueo biométrico

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; MUST; RF-03,RF-41,RF-42,RF-45.
- **Historia:** Como usuario que activó biometría, quiero desbloquear mis datos locales para proteger mis rutinas y sesión frente a otras personas.
- **Precondiciones:** sesión vigente y opción activada.
- **Criterio de aceptación:** Dada sesión vigente y biometría voluntariamente activada, cuando la autenticación biométrica tiene éxito, entonces accedo a datos locales permitidos; cancelación o rechazo no concede acceso y ofrece credencial alternativa. Un JWT expirado obliga a iniciar sesión con la API.
- **Flujo satisfactorio:** Prompt → acceso. **Alternativos:** No disponible/rechazo/cancelación.
- **Pantallas / API:** Config/Desbloqueo; GET /users/me cuando corresponda.
- **Pruebas previstas / etapa / estado:** UseCase, dispositivo físico; 15; PLANNED.

## HU23 · Push de eventos reales

- **Actor / proceso / prioridad / RF:** Usuario; P01/P02; MUST; RF-31–RF-35,RF-38,RF-39.
- **Historia:** Como atleta o entrenador, quiero recibir avisos de solicitudes y rutinas confirmadas para atender cambios relevantes sin vigilar la app constantemente.
- **Precondiciones:** dispositivo asociado a sesión.
- **Criterio de aceptación:** Dado dispositivo asociado a mi cuenta y permiso aplicable, cuando se confirma uno de los cuatro eventos de negocio, entonces FCM intenta entregar al destinatario autorizado y al tocar abre destino protegido. Rotación de token actualiza registro; logout desasocia; fallo de FCM no revierte la transacción de negocio.
- **Flujo satisfactorio:** Evento → aviso → destino. **Alternativos:** Permiso denegado/token renovado/sesión expirada.
- **Pantallas / API:** Notificaciones y destinos; PUT|DELETE /devices/{installation_id}.
- **Pruebas previstas / etapa / estado:** Backend fake Admin, E2E real; 17/18; PLANNED.

## HU24 · Calculadora corporal

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; SHOULD; RF-06,RF-07.
- **Historia:** Como usuario, quiero calcular IMC y necesidades energéticas estimadas para orientar mis metas personales de entrenamiento.
- **Precondiciones:** datos de entrada válidos.
- **Criterio de aceptación:** Dados peso, talla, edad, sexo de fórmula y actividad dentro de rango, cuando calculo, entonces obtengo IMC, TMB y mantenimiento según fórmulas documentadas sin enviar datos a la API. Valores inválidos muestran error y no resultado engañoso.
- **Flujo satisfactorio:** Ingresar → resultado. **Alternativos:** Dato fuera de rango.
- **Pantallas / API:** Calculadora; Ninguno.
- **Pruebas previstas / etapa / estado:** Unitarias, UI; 12; PLANNED.

## HU25 · Consultar rachas

- **Actor / proceso / prioridad / RF:** Atleta; P03; SHOULD; RF-09,RF-54.
- **Historia:** Como atleta, quiero ver mi racha actual y mi mejor racha para reconocer la constancia de mis hábitos.
- **Precondiciones:** P03 aprobado y días confirmados en API.
- **Criterio de aceptación:** Dado historial propio confirmado, cuando consulto rachas, entonces se derivan de días con al menos tres hábitos cumplidos, usando fecha local y sin contador duplicado; si no hay registros, ambas son cero.
- **Flujo satisfactorio:** Consultar historial → calcular → mostrar. **Alternativos:** Sin registros, error de red.
- **Pantallas / API:** Hoy/Historial; GET /habits/history.
- **Pruebas previstas / etapa / estado:** Fechas, zona, huecos y correcciones; 11.A/B; PLANNED.

## HU26 · Recordatorio diario local

- **Actor / proceso / prioridad / RF:** Atleta; P03; SHOULD; RF-36.
- **Historia:** Como atleta, quiero un recordatorio configurable de hábitos para revisar mi progreso cuando aún puedo completar mi meta.
- **Precondiciones:** preferencia activada y permiso aplicable.
- **Criterio de aceptación:** Dado progreso remoto reciente que confirma meta pendiente, cuando llega la hora configurada, entonces WorkManager muestra un aviso local; si el progreso no puede verificarse, evita afirmar que faltan hábitos. Logout cancela el trabajo.
- **Flujo satisfactorio:** Programar → verificar → avisar. **Alternativos:** Sin permiso, sin red, meta cumplida.
- **Pantallas / API:** Ajustes/Hoy; GET /habits/daily/{date}.
- **Pruebas previstas / etapa / estado:** Worker fake, fecha y permisos; 19; PLANNED.

## HU27 · Deshacer eliminación

- **Actor / proceso / prioridad / RF:** Entrenador; P02; SHOULD; RF-16.
- **Historia:** Como entrenador, quiero restaurar una rutina recién eliminada para corregir un borrado accidental.
- **Precondiciones:** eliminación lógica aprobada y ventana de restauración vigente.
- **Criterio de aceptación:** Dada rutina propia eliminada, cuando pulso Deshacer dentro de la ventana permitida, entonces la API restaura la misma rutina y permisos tras confirmar transacción; fuera de ventana o sin propiedad devuelve conflicto o prohibición sin inventar restauración.
- **Flujo satisfactorio:** Borrar → Snackbar → restaurar. **Alternativos:** Tiempo vencido, red, vínculo terminado.
- **Pantallas / API:** Rutinas entrenador; POST /routines/{id}/restore propuesto.
- **Pruebas previstas / etapa / estado:** Transacción y UI; 13; PROPOSED.

## HU28 · Compartir identificación pública mediante Intent

- **Actor / proceso / prioridad / RF:** Entrenador; P01; COULD; RF-30.
- **Historia:** Como entrenador, quiero compartir mi username o UUID público con una aplicación instalada para facilitar que un atleta lo introduzca en la búsqueda.
- **Precondiciones:** perfil autenticado.
- **Criterio de aceptación:** Dado mi identificador público visible, cuando pulso Compartir, entonces un Intent compatible ofrece solo username o UUID, sin email ni datos de atletas. Si no hay aplicación compatible, se informa el fallo sin ocultar la opción Must de copiar el identificador (HU32).
- **Flujo satisfactorio:** Abrir perfil → Compartir mediante Intent. **Alternativos:** Sin app de compartir; copiar por HU32.
- **Pantallas / API:** Perfil propio; GET /users/me ya implementado.
- **Pruebas previstas / etapa / estado:** UI e Intent; 07; PROPOSED.

## HU29 · Preferencias de notificaciones

- **Actor / proceso / prioridad / RF:** Usuario; Transversal; SHOULD; RF-37.
- **Historia:** Como usuario, quiero ajustar recordatorios y preferencias de avisos para recibir información útil sin interrupciones innecesarias.
- **Precondiciones:** sesión vigente.
- **Criterio de aceptación:** Dada preferencia modificada, cuando la guardo, entonces persiste por cuenta y el trabajo local se actualiza; desactivar recordatorios no impide los avisos FCM de negocio permitidos. Permisos, canal y registro de token necesarios para FCM siguen siendo Must en HU23.
- **Flujo satisfactorio:** Ajustes → guardar. **Alternativos:** Permiso denegado.
- **Pantallas / API:** Ajustes; DataStore local.
- **Pruebas previstas / etapa / estado:** DataStore y worker fake; 19; PLANNED.

## HU30 · Consultar rutinas propias

- **Actor / proceso / prioridad / RF:** Entrenador; P02; MUST; RF-12,RF-13.
- **Historia:** Como entrenador, quiero listar mis rutinas y abrir sus detalles para revisar a qué atleta está asignado cada plan.
- **Precondiciones:** sesión TRAINER.
- **Criterio de aceptación:** Dadas rutinas propias, cuando consulto lista y detalle, entonces la API filtra por propietario, muestra atleta/vínculo y ejercicios; una rutina ajena devuelve 403/404 y una lista vacía no se presenta como fallo.
- **Flujo satisfactorio:** Lista → detalle. **Alternativos:** Vacío, red, permiso.
- **Pantallas / API:** Rutinas entrenador/Detalle; GET /routines, GET /routines/{id}.
- **Pruebas previstas / etapa / estado:** Propiedad API y ViewModel; 08/10; PLANNED.

## HU31 · Consultar entrenador activo

- **Actor / proceso / prioridad / RF:** Atleta; P01; MUST; RF-25,RF-26.
- **Historia:** Como atleta, quiero consultar mi solicitud y entrenador activo para saber si ya puedo recibir rutinas.
- **Precondiciones:** sesión ATHLETE.
- **Criterio de aceptación:** Dada solicitud propia o vínculo activo, cuando abro Mi entrenador, entonces la API muestra estado y perfil público autorizado; sin vínculo ofrece estado vacío. No expone correo del entrenador ni relaciones ajenas; 401 requiere nueva sesión.
- **Flujo satisfactorio:** Mi entrenador → estado. **Alternativos:** Pendiente, rechazada, sin vínculo, red.
- **Pantallas / API:** Estado de solicitud/Mi entrenador; GET /coaching/requests/sent, GET /coaching/me.
- **Pruebas previstas / etapa / estado:** API, ViewModel y estados; 05/07; PLANNED.

## HU32 · Consultar y copiar identificación pública

- **Actor / proceso / prioridad / RF:** Entrenador; P01; MUST; RF-30.
- **Historia:** Como entrenador, quiero ver y copiar mi username o UUID público completo para que un atleta pueda buscarme y solicitar vinculación.
- **Precondiciones:** sesión TRAINER autenticada.
- **Criterio de aceptación:** Dada mi identidad autenticada, cuando abro la sección de identificación, entonces veo username y UUID propios, y Copiar deposita el valor completo aunque se abrevie visualmente. Ninguna acción copia email, hash ni datos de atletas; un usuario ajeno no puede consultar mi identidad privada.
- **Flujo satisfactorio:** Abrir identificación → copiar username o UUID → atleta puede buscar el valor.
- **Alternativos:** Portapapeles no disponible, sesión expirada.
- **Pantallas / API:** Identidad del entrenador y variante 07-10; GET /users/me implementado para identidad propia.
- **Pruebas previstas / etapa / estado:** ViewModel, portapapeles, autorización y búsqueda; 07; PARTIAL (backend de identidad propia implementado, UI pendiente).

## Priorización y trazabilidad

26 Must, 5 Should, 1 Could y 0 Won't en este inventario de historias. HU25–HU29 y HU30–HU32 cierran la cobertura de rachas, recordatorios, Deshacer, compartir, preferencias, rutinas propias y entrenador activo. El backlog completo y las exclusiones se definen en [requirements.md](requirements.md); no se fabrican historias para funciones descartadas. Contáctame cuenta ahora con vista independiente inspeccionada; Room tiene variantes offline inspeccionadas. El [inventario visual](design/mockup-inventory.md) registra las brechas restantes de las 26 HU Must, incluidas carga/error de Contactame y sesión expirada offline; disponer de una imagen no acredita la implementación. Cada historia Must se verificará con API real, estado UI y prueba indicada antes de declararse completada.

## Criterios vigentes de historias previas v6.0

La prioridad previa de HU01–HU32 se conserva. HU01/HU02/HU05 aceptan solo rol devuelto por servidor y registro invitado; HU08–HU13/HU31 usan catálogo y cuentas activas. HU14–HU17/HU30 incorporan fecha/unicidad diaria y Hoy combinado. HU18/HU19/HU21/HU25/HU26 pasan a cuatro hábitos/meta3/4, sin entrenar como quinto hábito. HU20 elimina entrada y marcado manual: sin permiso/sensor muestra limitación; solo observación confirmada alcanza caminar. Las alternativas manuales y metas3/5 en redacción histórica quedan sustituidas. Los escenarios de red/cancelación/401/403/409 y propiedad mantienen vigencia.

## HU33 · Registrarse con invitación
- **Actor / proceso / prioridad / RF:** Atleta o entrenador invitado; acceso a P01–P03; MUST; RF-01/RF-57.
- **Historia:** Como persona invitada por FitTrack Gym, quiero registrar mi cuenta con un código individual para acceder con el rol autorizado.
- **Precondiciones:** invitación vigente y cupo reservado.
- **Aceptación:** Dado código vigente, cuando envío identidad válida y contraseñas iguales sin role, entonces se crea cuenta y consume código atómicamente, y navego según rol devuelto. Código incorrecto/expirado/revocado/usado devuelve error genérico; role extra devuelve422; conflicto de usuario no consume la invitación. Código permanece solo en memoria móvil.
- **Vistas/API/pruebas/estado:** Registro actualizado; POST auth/register; HTTP, concurrencia y DTO; R2 IMPLEMENTED, E2E pendiente.

## HU34 · Autorizar altas y desactivar cuentas
- **Actor / proceso / prioridad / RF:** Operador privado del gimnasio (no rol móvil); transversal; MUST; RF-58/RF-59.
- **Historia:** Como operador autorizado, quiero emitir/revocar invitaciones y desactivar cuentas para controlar las altas del gimnasio.
- **Precondiciones:** acceso privado al servidor/CLI; configuración segura.
- **Aceptación:** Dado cupo disponible, cuando emito una invitación, entonces se reserva plaza y se muestra el código solo una vez tras commit. Listar no devuelve códigos/hashes. Dos emisiones concurrentes no exceden cupo. Revocada/expirada libera reserva; desactivar impide login y JWT anterior sin borrar usuario. No se altera automáticamente ninguna cuenta previa.
- **Vistas/API/pruebas/estado:** CLI sin pantalla/API admin; bloqueo PostgreSQL, caducidad, rollback y autorización; R2 IMPLEMENTED.

## HU35 · Cumplir la rutina diaria
- **Actor / proceso / prioridad / RF:** Atleta; P02/P03; MUST; RF-60/RF-61.
- **Historia:** Como atleta, quiero consultar y confirmar mi rutina asignada de hoy para registrar el entrenamiento sin confundirlo con mis hábitos personales.
- **Precondiciones:** sesión activa y asignación autorizada para la fecha.
- **Aceptación:** Dada rutina propia del día, cuando confirmo cumplimiento y la API persiste, entonces Hoy muestra rutina cumplida separada de la meta3/4. Sin rutina muestra vacío; un atleta ajeno recibe403/404; fallo/409 no se presenta como guardado. Dos confirmaciones no duplican cumplimiento.
- **Vistas/API/pruebas/estado:** Hoy/Detalle necesitan variantes; contrato de cumplimiento por definir antes de implementar; unicidad, propietario, HTTP y ViewModel; bloque rutinas/cumplimiento PLANNED.

## HU36 · Consultar historial de asignaciones
- **Actor / proceso / prioridad / RF:** Entrenador; P02/P03; MUST; RF-61.
- **Historia:** Como entrenador, quiero consultar por fecha mis rutinas asignadas y su cumplimiento para revisar el seguimiento de mis planes.
- **Precondiciones:** sesión TRAINER activa y propiedad de rutinas.
- **Aceptación:** Dadas asignaciones propias, cuando filtro fechas/atleta, entonces veo solo mi historial y cumplimiento confirmado, sin hábitos personales. Rutinas ajenas no se exponen; vacío y error/reintento son diferentes. Retención/autorización tras finalizar vínculos se concreta antes del bloque de historial.
- **Vistas/API/pruebas/estado:** Historial requiere referencia visual; contrato futuro; propiedad/filtros/fechas/UI; PLANNED.

Inventario actual: 36 HU únicas, 30 Must, 5 Should y 1 Could. Opiniones/puntuaciones figuran como backlog Should posterior al núcleo, sin implementar ni fabricar historias para completar conteos.
