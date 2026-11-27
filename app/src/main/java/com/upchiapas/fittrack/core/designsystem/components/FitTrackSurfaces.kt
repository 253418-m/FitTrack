package com.upchiapas.fittrack.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.upchiapas.fittrack.ui.theme.FitTrackColors

@Composable
fun FitTrackCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (onClick == null) {
        Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = FitTrackColors.SurfaceElevated), content = { content() })
    } else {
        Card(onClick = onClick, modifier = modifier, colors = CardDefaults.cardColors(containerColor = FitTrackColors.SurfaceElevated), content = { content() })
    }
}

enum class FitTrackChipVariant { Information, Success, Warning, Error, Neutral }

@Composable
fun FitTrackChip(text: String, modifier: Modifier = Modifier, variant: FitTrackChipVariant = FitTrackChipVariant.Neutral) {
    val color = when (variant) {
        FitTrackChipVariant.Information -> FitTrackColors.OnPrimaryContainer
        FitTrackChipVariant.Success -> FitTrackColors.Success
        FitTrackChipVariant.Warning -> FitTrackColors.Warning
        FitTrackChipVariant.Error -> FitTrackColors.Error
        FitTrackChipVariant.Neutral -> FitTrackColors.TextSecondary
    }
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = FitTrackColors.SurfaceElevated)) {
        Text(text = text, color = color, style = MaterialTheme.typography.labelMedium, modifier = Modifier.defaultMinSize(minHeight = 32.dp).padding(horizontal = 12.dp, vertical = 8.dp))
    }
}

fun initialsOf(name: String): String = name.trim().split(Regex("\\s+")).filter(String::isNotEmpty).take(2).map { it.first().uppercaseChar() }.joinToString("").ifEmpty { "?" }

@Composable
fun FitTrackAvatar(initials: String, modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(modifier = modifier.size(size).background(FitTrackColors.PrimaryContainer, CircleShape), contentAlignment = Alignment.Center) {
        Text(initials.trim().take(2).ifEmpty { "?" }.uppercase(), color = FitTrackColors.OnPrimaryContainer, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
    }
}
