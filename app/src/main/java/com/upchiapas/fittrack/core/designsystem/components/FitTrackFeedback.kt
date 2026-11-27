package com.upchiapas.fittrack.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import com.upchiapas.fittrack.ui.theme.FitTrackSpacing

fun normalizeProgress(progress: Float): Float = if (progress.isFinite()) progress.coerceIn(0f, 1f) else 0f

@Composable
fun FitTrackProgress(progress: Float, modifier: Modifier = Modifier, label: String? = null) {
    val safe = normalizeProgress(progress)
    Column(
        modifier = modifier.semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(safe, 0f..1f)
        },
        verticalArrangement = Arrangement.spacedBy(FitTrackSpacing.Small)
    ) {
        if (label != null) Text(label, style = MaterialTheme.typography.labelLarge)
        LinearProgressIndicator(
            progress = { safe },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FitTrackDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    destructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = { TextButton(onClick = onDismissRequest) { Text(dismissLabel) } }
    )
}

enum class FitTrackState { Loading, Empty, Error }

@Composable
fun FitTrackStateContent(
    state: FitTrackState,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.padding(FitTrackSpacing.Large), verticalArrangement = Arrangement.spacedBy(FitTrackSpacing.Small)) {
        if (state == FitTrackState.Loading) CircularProgressIndicator()
        if (icon != null) icon()
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (actionLabel != null && onAction != null) FitTrackButton(actionLabel, onAction)
    }
}

@Composable
fun FitTrackSnackbar(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data -> Snackbar(data) }
}
