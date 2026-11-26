# Integración de hardware planificada

Estado: propuesta v5.0; no hay adaptadores, permisos ni pantallas implementados.

## Flujo continuo: pasos

Atleta/habits usará SensorManager.TYPE_STEP_COUNTER. Un adaptador Android expondrá callbackFlow con trySend y awaitClose; registrará listener solo durante seguimiento activo y lo liberará al cancelar. El ViewModel recogerá StateFlow con lifecycle. Se requiere ACTIVITY_RECOGNITION desde API 29, detección de ausencia de sensor y alternativa manual. La lectura es acumulada desde reinicio: dominio puro calculará progreso por usuario/fecha, reinicios, duplicados y cambios de día. DataStore persistirá observaciones locales. No prometer medición continua de 24 horas.

## Respuesta única: biometría

Desbloqueo opcional con BiometricManager, BiometricPrompt, Keystore y CryptoObject cuando aplique. El adaptador ligado al ciclo de vida de presentation convertirá callbacks en suspend cancelable, distinguirá fallo no terminal/cancelación/error/éxito y evitará reanudar dos veces. No guardar Activity en singleton, huellas, fotografías ni contraseñas. JWT remoto y login por contraseña siguen disponibles; sesión remota expirada exige login. Evaluar FragmentActivity solo al implementar si la API elegida lo requiere.

## Actuador: háptica

VibratorManager API 31+ y Vibrator API 26–30, VibrationEffect y permiso VIBRATE cuando corresponda. Caso de uso suspend con try/finally cancelará motor, comprobará hasVibrator y evitará superposición. SUCCESS, ERROR y ACHIEVEMENT solo tras eventos reales, según preferencias; no por recomposición, errores repetitivos de campos ni duplicando vibración del sistema.

Contratos y casos de uso de dominio sin Android; Hilt conecta adaptadores. Los paquetes core/hardware/actuator, core/hardware/biometric y features/habits/data/hardware/steps se crearán cuando se implementen.
