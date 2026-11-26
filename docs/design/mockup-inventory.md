# Inventario de vistas para actualización en etapa 02

Referencia declarada: `FitTrack · Mockups MVP (2).pdf`. El PDF no está en este workspace; se registra el inventario aportado por el propietario, sin afirmar inspección visual ni colores exactos. Tema oscuro, superficies grises, rojo de acción, texto claro y navegación inferior son requisitos; mockups definitivos aún no aprobados.

| # | Vista | Rol | Cambio o estado necesario |
|---|---|---|---|
| 01 | Login | Ambos | Conservar referencia original; errores de credenciales/red. |
| 02 | Registro | Ambos | Agregar username, rol y errores junto al campo. |
| 03 | Hoy | Atleta | Hábitos, progreso, rachas y navegación inferior actualizada. |
| 04 | Calculadora | Ambos | Fórmulas y navegación según rol; resultados estimados. |
| 05 | Mis rutinas atleta | Atleta | Rutinas del vínculo activo y estado sin entrenador. |
| 06 | Detalle rutina atleta | Atleta | Solo lectura. |
| 07 | Mis rutinas entrenador | Entrenador | Propias, atleta asignado y vínculo terminado. |
| 08 | Mis atletas | Entrenador | Solo activos; incorporar solicitudes recibidas. |
| 09 | Detalle rutina entrenador | Entrenador | Editar y eliminar. |
| 10 | Formulario rutina | Entrenador | Solo atletas vinculados, ejercicios dinámicos, crear/editar. |
| 11 | Mi entrenador sin vínculo | Atleta | Estado sin entrenador y Buscar. |
| 12 | Buscar entrenador | Atleta | Username/ID, tarjetas; carga, vacío, error. |
| 13 | Perfil público entrenador | Atleta | Nombre, username, ID y solicitar. |
| 14 | Estado solicitud | Atleta | Pendiente, rechazada y cancelada; cancelar pendiente. |
| 15 | Mi entrenador activo | Atleta | Identidad, fecha y finalizar vínculo. |
| 16 | Solicitudes recibidas | Entrenador | Atleta, fecha, aceptar/rechazar. |
| 17 | Detalle atleta vinculado | Entrenador | Rutinas, asignar y desvincular. |
| 18 | Identidad entrenador | Entrenador | Copiar username e ID. |

Vistas 01–10 son el inventario original; 11–18 son ocho vistas nuevas propuestas. Las variantes sin vínculo/solicitud/vínculo activo pueden compartir Route y UiState; el inventario no exige 18 ViewModels.

Navegación inferior: atleta Hoy, Calculadora, Rutinas, Entrenador; entrenador Rutinas, Atletas, Calculadora. Solicitudes dentro de Atletas; identidad pública desde encabezado del entrenador. Diálogos pendientes: enviar/cancelar solicitud, aceptar/rechazar, abandonar/desvincular y eliminar rutina. Cada flujo necesitará carga, éxito confirmado, vacío/error y Snackbar cuando corresponda. Verificar contraste, 48 dp, semántica, texto escalable, teclado e insets en diseños actualizados.

## Ampliación v5.0 propuesta para etapa 02

Los 18 diseños anteriores se conservan. Actualizar Login, Hoy y encabezados principales con identidad oscura/gris/rojo; contemplar permisos rechazados, hardware ausente, errores y ejemplos de las cuatro clases de push más recordatorio local. Incorporar: 19 Configuración general; 20 Preferencias de notificaciones; 21 Configuración de recordatorio; 22 Configuración biométrica; 23 Desbloqueo biométrico; 24 Seguimiento de pasos. Total previsto: 24 referencias. La biometría usará diálogo real del sistema; no diseñar sustituto falso. No se actualizaron mockups visuales en esta etapa.
