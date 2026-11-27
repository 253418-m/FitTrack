package com.upchiapas.fittrack.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun style(size: Int, line: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = FontFamily.Default, fontWeight = weight, fontSize = size.sp, lineHeight = line.sp)

val FitTrackTypography = Typography(
    headlineLarge = style(28, 36, FontWeight.Bold),
    headlineMedium = style(24, 32, FontWeight.Bold),
    headlineSmall = style(22, 30, FontWeight.SemiBold),
    titleLarge = style(24, 32, FontWeight.SemiBold),
    titleMedium = style(18, 26, FontWeight.SemiBold),
    titleSmall = style(16, 24, FontWeight.SemiBold),
    bodyLarge = style(16, 24),
    bodyMedium = style(14, 22),
    bodySmall = style(12, 18),
    labelLarge = style(14, 20, FontWeight.SemiBold),
    labelMedium = style(12, 18, FontWeight.Medium),
    labelSmall = style(12, 16, FontWeight.Medium)
)
