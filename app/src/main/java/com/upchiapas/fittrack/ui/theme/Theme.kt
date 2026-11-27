package com.upchiapas.fittrack.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FitTrackColorScheme = darkColorScheme(
    primary = FitTrackColors.ContentAccent,
    onPrimary = FitTrackColors.Background,
    primaryContainer = FitTrackColors.PrimaryContainer,
    onPrimaryContainer = FitTrackColors.OnPrimaryContainer,
    secondary = FitTrackColors.TextSecondary,
    onSecondary = FitTrackColors.Background,
    secondaryContainer = FitTrackColors.SurfaceElevated,
    onSecondaryContainer = FitTrackColors.TextPrimary,
    tertiary = FitTrackColors.Success,
    onTertiary = FitTrackColors.Background,
    tertiaryContainer = FitTrackColors.SurfaceElevated,
    onTertiaryContainer = FitTrackColors.Success,
    background = FitTrackColors.Background,
    onBackground = FitTrackColors.TextPrimary,
    surface = FitTrackColors.Surface,
    onSurface = FitTrackColors.TextPrimary,
    surfaceVariant = FitTrackColors.SurfaceElevated,
    onSurfaceVariant = FitTrackColors.TextSecondary,
    surfaceTint = FitTrackColors.Primary,
    inverseSurface = FitTrackColors.InverseSurface,
    inverseOnSurface = FitTrackColors.InverseOnSurface,
    inversePrimary = FitTrackColors.PrimaryContainer,
    outline = FitTrackColors.Outline,
    outlineVariant = FitTrackColors.OutlineVariant,
    scrim = FitTrackColors.Background,
    surfaceBright = FitTrackColors.SurfaceElevated,
    surfaceDim = FitTrackColors.Background,
    surfaceContainerLowest = FitTrackColors.Background,
    surfaceContainerLow = FitTrackColors.Surface,
    surfaceContainer = FitTrackColors.Surface,
    surfaceContainerHigh = FitTrackColors.SurfaceElevated,
    surfaceContainerHighest = FitTrackColors.SurfaceElevated,
    error = FitTrackColors.Error,
    onError = FitTrackColors.Background,
    errorContainer = FitTrackColors.PrimaryContainer,
    onErrorContainer = FitTrackColors.Error
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun FitTrackTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            view.context.findActivity()?.let { activity ->
                val controller = WindowCompat.getInsetsController(activity.window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = FitTrackColorScheme,
        typography = FitTrackTypography,
        shapes = FitTrackShapes,
        content = content
    )
}
