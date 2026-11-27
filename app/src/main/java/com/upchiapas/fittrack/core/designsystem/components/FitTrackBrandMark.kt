package com.upchiapas.fittrack.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upchiapas.fittrack.ui.theme.FitTrackColors

@Composable
fun FitTrackBrandMark(modifier: Modifier = Modifier, size: Dp = 56.dp) {
    Canvas(modifier = modifier.size(size)) {
        val factor = this.size.minDimension / 56f
        scale(factor, factor, pivot = Offset.Zero) {
            drawRoundRect(FitTrackColors.Primary, size = Size(56f, 56f), cornerRadius = CornerRadius(12f))
            val heart = Path().apply {
                moveTo(28f, 40f)
                cubicTo(24f, 36f, 15f, 30f, 15f, 24f)
                cubicTo(15f, 15f, 24f, 12f, 28f, 19f)
                cubicTo(32f, 12f, 41f, 15f, 41f, 24f)
                cubicTo(41f, 30f, 32f, 36f, 28f, 40f)
                close()
            }
            drawPath(heart, FitTrackColors.TextPrimary, style = Stroke(width = 2.5f))
            val pulse = Path().apply {
                moveTo(17f, 27f)
                lineTo(24f, 27f)
                lineTo(27f, 32f)
                lineTo(30f, 23f)
                lineTo(33f, 27f)
                lineTo(39f, 27f)
            }
            drawPath(pulse, FitTrackColors.TextPrimary, style = Stroke(width = 2.5f))
        }
    }
}
