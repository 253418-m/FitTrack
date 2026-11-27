# Inventario de mockups aprobados · FitTrack v5.0

Referencia principal local (fuera del repositorio): `MockupsFitTrack.pdf` en el directorio hermano `FitTrack-references`. Contiene **27 páginas**, **24 vistas únicas**, dos variantes adicionales (páginas 1 y 5) y una página de cinco ejemplos de notificaciones (página 25). Las exportaciones `mockups/svg/` y `mockups/png/` están disponibles en el mismo directorio. Estos archivos no se versionan ni se usan como pantallas de producción.

| Vista | Página principal | Rol | Referencia |
|---|---:|---|---|
| 01 Inicio de sesión | 2 | Ambos | `mockups/svg/01. Inicio de sesión.svg`; PNG homónimo |
| 02 Registro | 3 | Ambos | `mockups/png/02. Registro.png` |
| 03 Hoy | 4 | Atleta | `mockups/svg/03. Hoy - Atleta.svg`; PNG homónimo |
| 04 Calculadora corporal | 6 | Ambos | `mockups/png/04. Calculadora corporal.png` |
| 05 Mis rutinas | 7 | Atleta | `mockups/png/05. Mis rutinas - Atleta.png` |
| 06 Detalle de rutina | 8 | Atleta | `mockups/png/06. Detalle de rutina - Atleta.png` |
| 07 Mis rutinas | 9 | Entrenador | `mockups/png/07. Mis rutinas - Entrenador.png` |
| 08 Mis atletas | 10 | Entrenador | `mockups/png/08. Mis atletas - Entrenador.png` |
| 09 Detalle de rutina | 11 | Entrenador | `mockups/png/09. Detalle de rutina - Entrenador.png` |
| 10 Formulario de rutina | 12 | Entrenador | `mockups/png/10. Formulario de rutina.png` |
| 11 Mi entrenador sin vínculo | 13 | Atleta | `mockups/png/11. Mi entrenador - Sin vínculo.png` |
| 12 Buscar entrenador | 14 | Atleta | `mockups/png/12. Buscar entrenador.png` |
| 13 Perfil público del entrenador | 26 | Atleta | Exportación `Html → Body.png` (exportación PNG inspeccionada) |
| 14 Estado de solicitud | 15 | Atleta | `mockups/png/14. Estado de solicitud.png` |
| 15 Mi entrenador activo | 16 | Atleta | `mockups/png/15. Mi entrenador - Vínculo activo.png` |
| 16 Solicitudes recibidas | 17 | Entrenador | `mockups/png/16. Solicitudes recibidas.png` |
| 17 Detalle de atleta vinculado | 18 | Entrenador | `mockups/png/17. Detalle de atleta vinculado.png` |
| 18 Identidad del entrenador | 27 | Entrenador | Exportación `Html → Body-1.png` (exportación PNG inspeccionada) |
| 19 Configuración general | 19 | Ambos | `mockups/png/19. Configuración general.png` |
| 20 Preferencias de notificaciones | 20 | Ambos | `mockups/svg/20. Preferencias de notificaciones.svg`; PNG homónimo |
| 21 Recordatorio diario | 21 | Atleta | `mockups/png/21. Configuración de recordatorio.png` |
| 22 Configuración biométrica | 22 | Ambos | `mockups/png/22. Configuración biométrica.png` |
| 23 Desbloqueo biométrico | 23 | Ambos | `mockups/png/23. Desbloqueo biométrico.png` |
| 24 Seguimiento de pasos | 24 | Atleta | `mockups/png/24. Seguimiento de pasos.png` |

La página 5 (`03. Hoy - Atleta-1.png`) es variante de navegación inferior; la página 4 define el contenido principal de Hoy. La página 1 (`20. Preferencias de notificaciones-1.png`) es variante; la página 20 es principal. La página 25 (`Ejemplos de notificaciones Android.png`) ilustra nueva solicitud, aceptación, rechazo, rutina asignada y recordatorio local; no es una pantalla adicional.

## Correcciones para las futuras pantallas

- Hoy: un solo encabezado; cinco hábitos binarios y meta de tres. El podómetro muestra pasos observados en sesiones activas y conserva alternativa manual. No se incorporan vasos, sueño, calorías o distancia como registros individuales.
- Buscar entrenador: resultados reales únicamente por username o UUID. Los identificadores cortos y resultados no coincidentes del mockup son ilustrativos.
- Preferencias: el rol procede de la sesión; retirar selector de rol y simuladores. Mostrar categorías correspondientes al rol y mantener permisos Android separados de preferencias.
- Biometría: disponible solo después de una sesión inicial habilitada; usar diálogo real del sistema. Las contraseñas de ejemplo no son datos de producción.
- Rutinas y vínculo: acciones solo según autorización y vínculo activo. No presentar estados vacíos junto con datos ni acciones inexistentes.
- Notificaciones: cuatro eventos remotos FCM y recordatorio local condicionado a meta pendiente; no prometer puntualidad ni mostrar información privada en pantalla bloqueada.

La etapa 02.2 implementa **componentes base**, no las 24 pantallas. Las capturas contienen datos ilustrativos y no definen nuevas reglas de negocio.
