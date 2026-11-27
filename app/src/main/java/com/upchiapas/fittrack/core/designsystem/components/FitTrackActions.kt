package com.upchiapas.fittrack.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.upchiapas.fittrack.ui.theme.FitTrackColors

enum class FitTrackButtonVariant { Primary, Secondary, Destructive }

@Composable
fun FitTrackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: FitTrackButtonVariant = FitTrackButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: (@Composable () -> Unit)? = null
) {
    val container = when (variant) {
        FitTrackButtonVariant.Primary -> FitTrackColors.Primary
        FitTrackButtonVariant.Secondary -> FitTrackColors.SurfaceElevated
        FitTrackButtonVariant.Destructive -> FitTrackColors.PrimaryContainer
    }
    val content = when (variant) {
        FitTrackButtonVariant.Primary -> FitTrackColors.OnPrimary
        FitTrackButtonVariant.Secondary -> FitTrackColors.TextPrimary
        FitTrackButtonVariant.Destructive -> FitTrackColors.Error
    }
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = FitTrackColors.SurfaceElevated,
            disabledContentColor = FitTrackColors.TextSecondary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.width(18.dp), color = content, strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        } else if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
