# Inventario de mockups aprobados v5.0 y FitTrack v5.1 aprobado

**Estado vigente 03.R.4 (27-11-2026):** alcance v5.1 aprobado expresamente por el usuario mediante D01–D11, registradas en [decisiones](../decisions.md). Las menciones anteriores a aprobación pendiente de esas decisiones son históricas y quedan sustituidas por este registro. Funcionalidades futuras: PLANNED; aprobación de alcance no acredita implementación, pruebas ni aprobación docente. Cobertura visual global parcial; verificaciones externas pendientes.

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

## Plan previo a la inspección 03.R.3

Se conservan las **24 referencias únicas** aprobadas; los elementos siguientes no son mockups existentes. [Historias Must](../user-stories.md) y [procesos](../business-processes.md) definen los wireframes faltantes. El sistema [Material 3](design-system.md) permanece sin cambios.

| Referencia | Acción para 03.R.3 | Tipo |
|---|---|---|
| Login, Registro, Ajustes | Añadir acceso Contactame desde Ajustes y errores servidor; Login público no aprobado | Modificación/variante |
| Rutinas atleta y detalle | Estado offline con última sincronización y reintento; estado sin cache separado de lista vacía | Variantes de 2 vistas existentes |
| Hoy y pasos | Distinguir dato observado, cambio pendiente y persistencia confirmada; fallo al guardar con reintento | Variantes existentes |
| Contactame | Formulario, carga, éxito, error e Intent disponible/no disponible | **Vista nueva propuesta** |
| Solicitudes y entrenador, editor de rutinas, notificaciones, biometría | Confirmar correspondencia con HU Must y estados vacío/error/permisos | Revisar referencias existentes; no contar nuevas hasta inspección |

La propuesta mínima aumenta el inventario de vistas únicas de 24 a **25** al aprobarse Contactame como pantalla independiente (D05); offline y errores son variantes, no vistas nuevas. En Stitch o Figma: duplicar la referencia oscura correspondiente, usar fondo oscuro, rojo acento y tipografía existente; componentes Material 3, contraste accesible y objetivos táctiles ≥48dp. Anotar en cada frame HU, estado, origen de datos, acción principal, error y reintento; enlazar frames de P01–P03 y exportar referencia para revisión. No marcar wireframe Must como completo antes de crear y revisar el frame real. No se generan mockups en 03.R.2.

## Matriz preliminar HU Must → referencia visual · revisión 03.R.2.1

La columna «base» identifica referencias existentes por número de la tabla anterior; **no acredita** que todos sus estados y criterios ya estén dibujados. «Pendiente» exige crear/revisar wireframe en 03.R.3. Las referencias 04 Calculadora, 18 Identidad, 20 Preferencias y 21 Recordatorio siguen disponibles para historias Should/Could.

| HU Must | Base existente | Variante o vista requerida | Aprobación del wireframe requerido |
|---|---|---|---|
| HU01 Registro | 02 | 422/409 y rol confirmado | Pendiente |
| HU02 Login | 01 | 401, carga, error y reintento | Pendiente |
| HU03 Sesión | 01, 03/07 | Restauración, expiración y red | Pendiente |
| HU04 Logout | 19 | Confirmación, limpieza y retorno sin Atrás | Pendiente |
| HU05 Destinos por rol | 03, 07 | Transición y 403 | Pendiente |
| HU06 Contáctame | ninguna | **Vista nueva**: formulario, carga, éxito, error | Pendiente |
| HU07 Intent contacto | 19 y nueva Contáctame | Canal autorizado o ausencia de app | Pendiente |
| HU08 Buscar entrenador | 12, 13 | Vacío, error y perfil privado minimizado | Pendiente |
| HU09 Solicitar vínculo | 13, 14 | PENDING/409, carga y fallo | Pendiente |
| HU10 Resolver solicitud | 16 | Aceptar/rechazar, 409 y vacío | Pendiente |
| HU11 Cancelar solicitud | 14 | Cancelación confirmada y error | Pendiente |
| HU12 Terminar vínculo | 15, 17 | Confirmación, revocación y error | Pendiente |
| HU13 Mis atletas | 08, 17 | Vacío, carga, 403 y error | Pendiente |
| HU14 Crear/asignar rutina | 10, 17 | Validación, guardado, vínculo vencido | Pendiente |
| HU15 Rutina atleta | 05, 06 | Vacío, carga, acceso revocado | Pendiente |
| HU16 Editar/eliminar | 09, 10 | Confirmación borrar y conflicto | Pendiente |
| HU17 Room offline | 05, 06 | Offline lista/detalle; sin caché; sesión offline expirada; reconexión | Pendiente |
| HU18 Registrar/corregir hábito | 03 | Persistencia remota, 409, error al guardar | Pendiente |
| HU19 Progreso diario | 03 | Día sin registro, confirmado y fallo API | Pendiente |
| HU20 Pasos | 24, 03 | Progreso confirmado, permiso, reinicio y manual | Pendiente |
| HU21 Háptica | 03 | Confirmación visual tras guardado; vibración se verifica físicamente | Pendiente |
| HU22 Biometría | 22, 23 | JWT expirado, cancelación y alternativa | Pendiente |
| HU23 FCM | pág. 25, 14, 16, 05 | Permiso/canal, sesión expirada y destino protegido | Pendiente |
| HU30 Rutinas propias | 07, 09 | Lista vacía, error, detalle y propiedad | Pendiente |
| HU31 Entrenador activo | 11, 14, 15 | Sin vínculo, pendiente, rechazado, expiración | Pendiente |

**Inventario exacto propuesto:** una vista independiente nueva (Contáctame) y variantes de vistas existentes; 24 vistas actuales, 25 solo si se aprueba y dibuja Contáctame. No se cuentan estados como pantallas adicionales. Las bases 01–24 se mapearon por el inventario; en esta corrección no se inspeccionaron visualmente las 24 imágenes una por una. La etapa 03.R.3 debe cotejar cada frame real con esta matriz y registrar aprobación.

**Instrucción para Stitch/Figma:** crear frame Contáctame con identidad autenticada precargada no editable, asunto, mensaje, botón Enviar y correo ACTION_SENDTO solo tras disponer de dirección autorizada; duplicar 19 para acceso desde Ajustes. Duplicar 05/06 para offline con última sincronización, sin caché y JWT expirado (bloqueo); duplicar 03 para guardado pendiente/confirmado/409; duplicar 24 para pasos observados. En cada variante anotar HU, fuente API/Room, acción, error y reintento, usando Material 3 oscuro, rojo existente, contraste y controles ≥48 dp. No declarar aprobación hasta revisión del frame exportado.

## Inspección visual 03.R.3

Se inspeccionaron visualmente los 13 PNG v5.1 y las 24 vistas PNG originales, además de dos variantes y la lámina de notificaciones. Existen 13 SVG homónimos v5.1 localizados, sin renderizado individual. La dirección visual base fue aceptada con ajustes; la cobertura de HU Must sigue parcial. Las tablas preliminares anteriores expresan necesidades previas a recibir estas imágenes; el resultado observado y sus límites se registran a continuación. Los archivos gráficos permanecen fuera del repositorio.

### Trece referencias actualizadas inspeccionadas

Ruta externa: `FitTrack-references/mockups-v5.1/`. Cada nombre siguiente existe en `png/` y tiene SVG homónimo en `svg/`. «Revisada con ajustes» significa aceptación general de la dirección visual, no aprobación de todos los estados.

| ID · nombre real del archivo PNG | Vista y rol | HU / RF / proceso | Estado y ajuste de implementación |
|---|---|---|---|
| 01 · `01. Variantes - Autenticación y Acceso.png` | Login, carga; ambos | HU02–03; RF-02–03; transversal | Revisada con ajustes: conservar marca original, texto blanco en botón, un indicador de carga; faltan 401, red y expiración. |
| 03 · `03. Hoy - Atleta.png` | Hoy; atleta | HU18–19/HU21/HU25; RF-08–09/RF-40/RF-53–55; P03 | Revisada con ajustes: cinco hábitos y 3/5; separar manual/sensor/API. Faltan guardar/error/409/día ausente y confirmar restablecimiento. |
| 05.A · `05.A Mis rutinas sin conexión (con caché).png` | Lista offline; atleta | HU17; RF-49–52; P02 | Revisada con ajustes: cambiar «Sincronizado» por «Guardado localmente», última actualización real; quitar descansos no contratados. |
| 05.B · `05.B Sin conexión y sin caché.png` | Sin copia; atleta | HU17; RF-50/RF-56; P02 | Revisada con ajustes: Reintentar sí; quitar diagnóstico y controles Wi-Fi/modo avión; distinguir lista vacía válida y sesión expirada. |
| 05.C · `05.C Actualizando rutinas.png` | Reconexión; atleta | HU17; RF-51/RF-56; P02 | Revisada con ajustes: identificar tarjetas previas como caché; sin porcentaje ficticio ni éxito antes de respuesta. |
| 05.D · `05.D Acceso revocado.png` | Recurso retirado; atleta | HU12/HU17; RF-27–28/RF-52; P01/P02 | Revisada con ajustes: ocultar contenido revocado; quitar cargas/notas ajenas al contrato; limpiar copia cuando se conoce revocación. |
| 06.A · `06.A Detalle de rutina offline.png` | Detalle solo lectura; atleta | HU15/HU17; RF-10–11/RF-49–52; P02 | Revisada con ajustes: fecha real y sin edición; foto decorativa no implica almacenamiento multimedia; revisar iconos de candado. |
| 07-10 · `07-10. Variantes - Rutinas y Atletas.png` | **Solo** vacío al crear rutina sin atletas; entrenador | HU13–14/HU28/HU32; RF-14/RF-17/RF-30; P01/P02 | Revisada con ajustes: no acredita cuatro vistas. Copiar UUID completo Must; Compartir perfil Could, no acción principal. Sin QR, plantillas ni certificaciones. |
| 11-16 · `11-16. Variantes - Vinculación Atleta-Entrenador.png` | **Solo** búsqueda sin resultados; atleta | HU08/HU31; RF-19–20/RF-26; P01 | Revisada con ajustes: no acredita demás estados; definir normalización de @username, buscar solo username/UUID. |
| 19 · `19. Configuración general (Atleta y Entrenador).png` | Imagen visible **solo de atleta** | HU04/HU06–07/HU22–23/HU26/HU29; RF-04/RF-37/RF-41–42/RF-47–48; transversal | Revisada con ajustes: Contactame aparece; variante entrenador no comprobada; recordatorio/pasos solo atleta, rol inmutable. |
| 20-23 · `20-23. Variantes - Biometría y Notificaciones.png` | **Solo** biometría no disponible; ambos | HU22–23; RF-38–39/RF-41–42; transversal | Revisada con ajustes: no acredita FCM. Separar sensor ausente, temporal y sin configurar; diálogo nativo y contraseña de cuenta como alternativa. |
| 24 · `24. Seguimiento de pasos.png` | Seguimiento activo; atleta | HU18/HU20; RF-43–45/RF-53–55; P03 | Revisada con ajustes: meta 8000 y opción manual; faltan permiso rechazado, sensor ausente, detenido y guardado pendiente/confirmado. |
| 25 · `25. Contáctame.png` | **Vista independiente nueva**; ambos | HU06–07; RF-47–48; transversal | Revisada con ajustes: identidad solo lectura y límites 120/2000; quitar versión de app y promesa 24–48 h; faltan validación, carga/201/error y medio autorizado. |

**Conteo de vistas:** 24 originales preservadas + Contáctame = **25 únicas**. Los otros 12 PNG nuevos son variantes o láminas compuestas, no pantallas adicionales. Los SVG solo se localizaron; la inspección visual y las observaciones proceden de PNG. Los mockups son referencias: API/RF prevalecen ante contenido ilustrativo añadido por Stitch.

### Cobertura de las 26 HU Must tras inspección

`BASE_REVIEWED` = existe base principal inspeccionada; `PARTIAL` = faltan estados relevantes; `REUSABLE_COMPONENT` = un componente existente puede comunicar el resultado sin nuevo frame; `MISSING` = falta referencia suficiente; `NOT_VERIFIED` = no se pudo inspeccionar. Ninguno de estos estados implica funcionalidad implementada.

| HU | Base original / v5.1 | Estado visual | Brecha o componente aplicable |
|---|---|---|---|
| HU01 | 02 | PARTIAL | Registro: respuesta 409/422 y carga mediante error/formulario reutilizable. |
| HU02 | 01 / 01 | PARTIAL | Carga vista; 401 y red mediante error reutilizable. |
| HU03 | 01, 03, 07 / 01 | MISSING | Sesión expirada **sin conexión** y retorno seguro. |
| HU04 | 19 / 19 | PARTIAL | Diálogo de logout y limpieza de back stack. |
| HU05 | 03, 07 | BASE_REVIEWED | Destinos por rol; 403 mediante error protegido reutilizable. |
| HU06 | 19 / 19, 25 | PARTIAL | Formulario visto; carga, 201 y error con reintento. |
| HU07 | 25 | PARTIAL | Estado sin app visto; falta dirección autorizada e Intent real. |
| HU08 | 12, 13 / 11-16 | PARTIAL | Búsqueda vacía vista; normalización @username y error. |
| HU09 | 13, 14 | PARTIAL | Envío y pendiente; 409 y red mediante mensaje reutilizable. |
| HU10 | 16 | PARTIAL | Aceptar/rechazar; faltan confirmación y conflicto. |
| HU11 | 14 | PARTIAL | Cancelar visible; falta confirmación/resultados. |
| HU12 | 15, 17 / 05.D | PARTIAL | Fin visible; falta confirmación y revocación del vínculo. |
| HU13 | 08 / 07-10 | PARTIAL | Lista original; nueva imagen solo vacío de creación, no vacío de Mis atletas. |
| HU14 | 10 / 07-10 | PARTIAL | Formulario original; falta vínculo vencido y resultado guardado. |
| HU15 | 05, 06 / 06.A | BASE_REVIEWED | Lista/detalle; vacío y error con componentes existentes, autorización se prueba aparte. |
| HU16 | 07, 09, 10 | PARTIAL | Editar/borrar; falta confirmación de eliminación. |
| HU17 | 05, 06 / 05.A–D, 06.A | PARTIAL | Variantes offline vistas; **falta expiración JWT offline** y etiquetas corregidas. |
| HU18 | 03 / 03, 24 | MISSING | Falta diálogo de restablecimiento y estados guardar/error/409. |
| HU19 | 03 / 03 | PARTIAL | Progreso base; falta día sin registro y error API. |
| HU20 | 24 / 24 | PARTIAL | Activo visto; faltan permiso rechazado, sensor ausente y detenido. |
| HU21 | 03 / 03 | REUSABLE_COMPONENT | Éxito confirmado reutilizable; vibración se demuestra en dispositivo físico. |
| HU22 | 22, 23 / 20-23 | PARTIAL | No disponible visto; faltan no configurado, temporal, cancelar y éxito. |
| HU23 | 20 y lámina pág. 25 / 20-23 | PARTIAL | Cuatro eventos en lámina original; nueva imagen no muestra FCM; falta permiso y destino protegido. |
| HU30 | 07, 09 | BASE_REVIEWED | Lista/detalle propios; vacío/error reutilizables. |
| HU31 | 11, 14, 15 / 11-16 | PARTIAL | Bases de vínculo; nueva imagen solo búsqueda vacía; error/expiración pendientes. |
| HU32 | 18 / 07-10 | PARTIAL | Identidad y Copiar visibles; comprobar UUID completo al copiar; Compartir es HU28 Could. |

**Wireframes obligatorios aún insuficientes:** expiración offline (HU03/HU17); confirmación de restablecimiento y fallo/409 de hábitos (HU18); configuración de entrenador (HU04/HU06/HU22/HU23); permiso/sensor de pasos (HU20); separación de estados biométricos (HU22); contacto carga/éxito/error (HU06); solicitud y fin con confirmación (HU10–HU12); permiso FCM y destino protegido (HU23). No hace falta un frame por código HTTP: error, reintento, confirmación y carga pueden reutilizar componentes del sistema de diseño si el wireframe muestra el comportamiento relevante. La cobertura visual **permanece parcial**; la dirección de diseño base está revisada y las 13 referencias están registradas.

## Complemento visual 03.R.3.1

Se inspeccionaron únicamente los cuatro PNG nuevos. Inventario recursivo: **17 PNG y 17 SVG**, con nombres base correspondientes, sin versiones duplicadas u obsoletas identificadas en la carpeta. Los SVG se localizaron, sin renderizado individual. Se mantienen **25 vistas únicas**: estas cuatro referencias son variantes.

| Referencia y nombre real PNG (SVG homónimo) | Rol / proceso / trazabilidad | Resultado |
|---|---|---|
| 03.E · `03.E Hábitos - Error de guardado.png` | Atleta / P03 / HU18, RF-08/RF-53/RF-55/RF-56 | Aceptada: cinco hábitos, meta 3/5, 2/5 confirmados y cambio fallido separado; Reintentar/Descartar. No incrementar progreso ni encolar escrituras offline automáticamente. |
| 03.F · `03.F Hábitos - Conflicto de actualización.png` | Atleta / P03 / HU18, RF-55/RF-56 | Aceptada: cinco hábitos, Progreso desactualizado, Recargar progreso y bloqueo temporal. Representa 409; etiquetar cifras como última información conocida y nunca sobrescribir concurrentemente. |
| 03.G · `03.G Hábitos - Restablecer registro diario.png` | Atleta / P03 / HU18, RF-55 | Diálogo aceptado: Cancelar/Restablecer y advertencia de rachas/historial. DELETE exige confirmación; no aprueba política física/lógica de borrado. |
| 19.B · `19.B Configuración - Entrenador.png` | Entrenador / transversal / HU04/HU06/HU22/HU23, RF-04/RF-37/RF-41/RF-47 | Aceptada con ajustes: cambiar Detalle De Rutina por Configuración, Rol asignado verificado por Rol de la cuenta y retirar versión. No muestra recordatorio ni pasos. No se solicita rediseño. |

03.E y 03.F corresponden a las versiones corregidas observadas: mantienen los cinco hábitos originales y distinguen error/409 del progreso confirmado. No se encontraron alternativas obsoletas con esos IDs en el inventario actual.

### Actualización de cobertura (prevalece sobre brechas anteriores)

Las 26 HU Must siguen mapeadas. HU18 pasa de MISSING a **PARTIAL**: ya existen error, conflicto y confirmación de restablecimiento; faltan guardado en progreso y resultado del día sin registro. La configuración de entrenador pasa de no comprobada a **BASE_REVIEWED** con ajustes 19.B. Esto mejora HU04/HU06/HU22/HU23 sin demostrar sus operaciones.

| Caso restante | Estado | Evidencia y acción mínima |
|---|---|---|
| Sesión expirada offline · HU03/HU17 | MISSING | Falta referencia que bloquee lectura y ofrezca login/conexión. |
| Permiso de actividad rechazado/sensor ausente · HU20 | PARTIAL | 24 ofrece alternativa manual; falta representar rechazo permanente/Ajustes y ausencia real. |
| Contactame carga/éxito/error · HU06 | REUSABLE_COMPONENT | Formulario 25 más carga, error/reintento y confirmación existentes pueden representar estados; texto de éxito debe decir Mensaje registrado correctamente. Falta cotejo visual de composición, sin nueva pantalla independiente. |
| Confirmaciones de solicitudes/fin · HU10–HU12 | REUSABLE_COMPONENT | Diálogo confirmado 03.G aporta patrón Cancelar/acción; reutilizar con texto específico y resultado, sin frame por código HTTP. |
| Estados biométricos · HU22 | PARTIAL | 22/23 y 20-23 cubren base/no disponible; diferenciar temporal, sin configurar, cancelación y éxito mediante interfaz nativa. |
| Permiso FCM y destino protegido · HU23 | PARTIAL | Lámina original cubre cuatro eventos; falta referencia de permiso y sesión expirada al abrir destino. |

La cobertura visual global permanece **PARCIAL**. Los componentes reutilizables resuelven forma de presentación, no acreditan ejecución ni sustituyen las pruebas de autorización, persistencia o hardware.
