# Integración de hardware planificada

**Estado vigente R1–R2 · FitTrack Gym v6.0 (27-11-2026):** alcance aprobado por el usuario y aclaraciones docentes aportadas por él. Sustituye expresamente las reglas incompatibles v5.1 de registro libre/selección de rol, cinco hábitos/meta 3 de 5 y cumplimiento manual de pasos. Las secciones previas incompatibles se conservan como evidencia histórica, sin vigencia normativa. No se atribuye una inspección nueva de material docente. Implementación actual: autenticación e invitaciones; procesos de negocio, Room, FCM, hardware, Contactame y HTTPS siguen pendientes.

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

Estado: propuesta documental v5.1; no hay adaptadores, permisos ni pantallas implementados.

## Flujo continuo: pasos

Atleta/habits usará SensorManager.TYPE_STEP_COUNTER. Un adaptador Android expondrá callbackFlow con trySend y awaitClose; registrará listener solo durante seguimiento activo y lo liberará al cancelar. El ViewModel recogerá StateFlow con lifecycle. Se requiere ACTIVITY_RECOGNITION desde API 29, detección de ausencia de sensor y alternativa manual. La lectura es acumulada desde reinicio: dominio puro calculará progreso por usuario/fecha, reinicios, duplicados y cambios de día. DataStore persistirá observaciones locales. No prometer medición continua de 24 horas.

## Respuesta única: biometría

Desbloqueo opcional con BiometricManager, BiometricPrompt, Keystore y CryptoObject cuando aplique. El adaptador ligado al ciclo de vida de presentation convertirá callbacks en suspend cancelable, distinguirá fallo no terminal/cancelación/error/éxito y evitará reanudar dos veces. No guardar Activity en singleton, huellas, fotografías ni contraseñas. JWT remoto y login por contraseña siguen disponibles; sesión remota expirada exige login. Evaluar FragmentActivity solo al implementar si la API elegida lo requiere.

## Actuador: háptica

VibratorManager API 31+ y Vibrator API 26–30, VibrationEffect y permiso VIBRATE cuando corresponda. Caso de uso suspend con try/finally cancelará motor, comprobará hasVibrator y evitará superposición. SUCCESS, ERROR y ACHIEVEMENT solo tras eventos reales, según preferencias; no por recomposición, errores repetitivos de campos ni duplicando vibración del sistema.

Contratos y casos de uso de dominio sin Android; Hilt conecta adaptadores. Los paquetes core/hardware/actuator, core/hardware/biometric y features/habits/data/hardware/steps se crearán cuando se implementen.

## Alineación propuesta v5.1

La especificación exige **tres tipos distintos de hardware**, sin imponer una categoría de cada uno. Ninguna integración está implementada; se validará en dispositivo físico. Arquitectura común: interfaz en `domain`, DataSource y RepositoryImpl en `data`, UseCase consumido por ViewModel, provisión Hilt. Fakes prueban disponibilidad, cancelación, permiso y errores; Compose solo observa UiState. Ver [P03](business-processes.md) y [HU](user-stories.md).

| Tipo | Valor y proceso | Permiso / manejo | Ciclo y prueba |
|---|---|---|---|
| VibratorManager / Vibrator | Confirmar una meta P03 **persistida**; nunca por recomposición | `VIBRATE` manifiesto; comprobar existencia, sin permiso runtime | VibrationEffect, cancelación y `try/finally`; fake y equipo físico |
| BiometricPrompt / Keystore | Proteger sesión y rutinas privadas P02; activación voluntaria | BiometricManager, disponibilidad, rechazo y credencial alternativa; no almacenar muestra ni contraseña | `suspendCancellableCoroutine`, cancelar prompt, no prolongar JWT; fake y equipo físico |
| TYPE_STEP_COUNTER | Progreso P03 durante observación real | `ACTIVITY_RECOGNITION` cuando aplique; rechazo, rechazo permanente, Ajustes y entrada manual | `callbackFlow/awaitClose`, liberar listener; baseline por usuario/fecha/reinicio; fake y equipo físico |

Los pasos observados no son una lectura continua garantizada 24 horas. No guardar lecturas crudas indefinidamente. La biometría podría evaluarse como función transversal; confirmar su aceptación docente. Escaneo QR por cámara para identificar entrenador es alternativa documentada, no integración adicional autorizada.

## HW03 vigente v6.0 · sin implementación en R1–R2

Se sustituye toda alternativa manual previa: usuario no ingresa pasos ni marca walk_completed. SensorManager TYPE_STEP_COUNTER, callbackFlow/trySend/awaitClose, DataSource/RepositoryImpl/UseCase/Hilt; ACTIVITY_RECOGNITION cuando corresponda. Sin sensor, permiso rechazado o permanentemente rechazado, explicar limitación y mantener objetivo pendiente (Ajustes solo cuando aplique). Meta 8000; servidor deriva cumplimiento.

Gestionar baseline/reinicio/fecha/cuenta/dispositivo, valor absoluto diario e identidad de observación para evitar duplicados; no sumar dispositivos indiscriminadamente. Sin red mostrar observado local separado de confirmado; al recuperar conexión con sesión vigente sincronizar día actual de forma idempotente. No hay promesa 24 h ni antifraude absoluto. Vibración: meta personal 3/4 confirmada; biometría voluntaria para proteger sesión, sin extender JWT. Mantener cancelación/ciclo de vida/pruebas físicas en bloques posteriores.
