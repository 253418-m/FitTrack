# Sistema de diseño · FitTrack v5.0

## Objetivo y referencias

Base Compose reutilizable para las futuras pantallas de atleta y entrenador. Se consultaron estructuralmente los SVG de inicio de sesión, Hoy y preferencias; se visualizaron PNG de inicio de sesión, Hoy, preferencias y búsqueda. El PDF aprobado `MockupsFitTrack.pdf` está disponible en el directorio hermano `FitTrack-references`; su mapeo de páginas procede del contexto aprobado. No se importan mockups completos ni código web.

Los SVG revisados usan fondo `#131313`, superficies `#201F1F`/`#2A2A2A` y rojo `#DC2626`. El tema usa tokens cercanos `#141414`, `#202020` y `#292929` para mantener roles coherentes. El verde y ámbar se reservan para estados semánticos, nunca como único indicador. El símbolo de corazón con pulso de la referencia de autenticación se adaptó como dibujo Compose individual (`FitTrackBrandMark`) y aparece en la pantalla temporal.

## Tema y tokens

Un único `FitTrackTheme` en `ui/theme` aplica `darkColorScheme`, tipografía y formas Material 3. El tema es oscuro fijo y no usa dynamic color. Se conserva el paquete existente para evitar trasladar la Activity y duplicar el tema.

| Rol | Valor |
|---|---|
| Background / Surface / Elevated | `#141414` / `#202020` / `#292929` |
| Rojo de marca / acento textual / texto sobre botón de marca | `#DC2626` / `#FF7777` / `#FFFFFF` |
| Acento alternativo reservado | `#EF4444` |
| PrimaryContainer / OnPrimaryContainer | `#442020` / `#FFB4AB` |
| TextPrimary / TextSecondary | `#F5F5F5` / `#B3B3B3` |
| Outline interactivo / divisor decorativo | `#777777` / `#353535` |
| Success / Warning / Error | `#22C55E` / `#F59E0B` / `#FFB4AB` |

Los roles Material 3 primary, secondary, tertiary, background, surface, surfaceVariant, surfaceTint, inverseSurface, inverseOnSurface, inversePrimary, outline, outlineVariant, scrim, error y la familia surfaceContainer se definen explícitamente con tokens FitTrack. El rojo de marca `#DC2626` se conserva como fondo del botón principal con texto blanco (4.83:1). Para texto y componentes Material que usan `colorScheme.primary` —TextButton, acciones de AlertDialog, etiqueta y borde de campo enfocado— se usa `#FF7777`: sobre Surface y SurfaceElevated alcanza 6.33:1 y 5.65:1, frente a 3.37:1 y 3.01:1 del rojo de marca. `onPrimary` es `#141414` para el nuevo rol; el botón de marca conserva explícitamente blanco. La acción de Snackbar sobre la superficie inversa utiliza `inversePrimary` `#442020`, con 13.07:1 respecto a `#F5F5F5`. El borde interactivo cambió de `#555555` a `#777777`: sobre Background, Surface y SurfaceElevated pasó de 2.47/2.19/1.95:1 a 4.11/3.64/3.25:1. `#353535` queda como `outlineVariant` para divisores decorativos. Texto principal sobre fondo: 16.90:1; texto secundario sobre superficie: 7.77:1; éxito y advertencia sobre SurfaceElevated: 6.38 y 6.77:1; error sobre su contenedor: 8.40:1. Son cálculos WCAG de pares principales, no una certificación completa.

Tipografía sans serif del sistema, sin fuentes externas: títulos 28/24/18/16 sp; cuerpo 16/14/12 sp; etiquetas 14/12 sp, con alturas de línea explícitas. Espaciados: 4, 8, 12, 16, 24 y 32 dp. Formas Material 3: 8, 12, 12, 16 y 24 dp. El margen principal previsto es 16 dp. Los textos usan sp y los layouts dp.

## Componentes y estado

`core/designsystem/components` contiene marca gráfica, botón (principal, secundario, destructivo, deshabilitado y carga), campo de texto, campo de contraseña, tarjeta, chip, avatar, barra superior, barra inferior visual, progreso, diálogo, contenido de carga/vacío/error y host de Snackbar. Valores y callbacks proceden del consumidor. El sistema de diseño no depende de ViewModels, repositorios, navegación, red, sensores ni modelos de dominio.

El botón bloquea pulsaciones durante carga. El campo de contraseña conserva el valor en el consumidor y solo mantiene localmente la visibilidad; no guarda el contenido. `normalizeProgress` limita valores fuera de 0..1 y convierte valores no finitos en 0. `initialsOf` admite nombres vacíos. La barra inferior recibe identificadores visuales, iconos como slots y un callback, sin rutas ni `NavController`. Los iconos definitivos se elegirán por feature con un estilo uniforme; los glifos de Preview son ilustrativos.

`FitTrackCard` solo es interactiva cuando recibe `onClick`. Chips son informativos y incluyen texto. Material 3 se usa directamente para Switch, Checkbox, Snackbar, NavigationBar, AlertDialog y barras de progreso. Los futuros features deben reutilizar tokens y componentes antes de crear variantes específicas.

## Previews y pruebas

`core/designsystem/preview/DesignSystemPreview.kt` muestra variantes generales, navegación de ambos roles y ancho reducido con escala de fuente aumentada. Sus datos no aparecen en la aplicación de producción. FitTrackApp conserva solo la pantalla temporal.

La validación previa de 02.2 registró 3 pruebas unitarias y 11 instrumentadas aprobadas en Pixel 10 Pro XL AVD (`emulator-5554`, Android 17): 10 del sistema de diseño y 1 previa. En la revisión 02.3 se amplió la interacción táctil de botones deshabilitados y en carga, y la comprobación de ocultar, mostrar y volver a ocultar contraseña. En esta ejecución `assembleDebug`, 3 pruebas unitarias, `lintDebug` (12 advertencias, 0 errores) y compilación instrumentada aprobaron. En la recuperación 02.3, el emulador autorizado Pixel 10 Pro XL AVD (`emulator-5554`, Android 17) estuvo disponible. La primera ejecución detectó una aserción incorrecta: `InputText` conserva el valor editable aunque `EditableText` esté enmascarado. La prueba se corrigió para comprobar `EditableText`; la segunda ejecución aprobó 11/11 pruebas, sin fallos, errores ni omisiones. `assembleDebug`, 3 pruebas unitarias y `lintDebug` volvieron a aprobar. Se instaló y ejecutó el APK actual: `MainActivity` mostró fondo oscuro, marca y nombre centrados, iconos claros en barras del sistema y ninguna superposición ni crash observados. Captura actual inspeccionada: `FitTrack-references/evidence/stage-02/fittrack-main-final.png`; la captura anterior se conservó como evidencia histórica. Se renderizaron e inspeccionaron previamente las previews generales, navegación de ambos roles, texto escalado, barra superior y estados. En 02.3 se volvió a renderizar e inspeccionar `DesignSystemPreview`, que mostró el nuevo acento textual y mantuvo el botón rojo de marca. `DestructiveDialogPreview` volvió a responder «Failed to get rendered image»; su apariencia actual sigue sin inspección visual directa. El backend conservó 2 pruebas aprobadas y `pip check` satisfactorio en la revisión anterior; persiste la advertencia Starlette/httpx reservada para etapa 03.

## Accesibilidad y límites

Objetivos interactivos de al menos 48 dp en el botón; las acciones Material 3 usan sus mínimos táctiles. Etiquetas visibles, mensajes de error en texto, selección semántica de NavigationBarItem y progreso semántico. Evitar significado solo por color y probar escalado de fuente, lector de pantalla, teclado e insets en las futuras pantallas completas. El sistema de diseño no certifica accesibilidad integral ni fidelidad pixel a pixel de las 24 vistas.
