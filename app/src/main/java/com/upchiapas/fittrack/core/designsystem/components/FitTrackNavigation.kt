package com.upchiapas.fittrack.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.upchiapas.fittrack.ui.theme.FitTrackColors

data class FitTrackDestination(
    val id: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    backIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        modifier = modifier,
        navigationIcon = {
            if (onBack != null && backIcon != null) {
                IconButton(onClick = onBack, modifier = Modifier.semantics { contentDescription = "Volver" }) { backIcon() }
            }
        },
        actions = actions
    )
}

@Composable
fun FitTrackBottomBar(
    destinations: List<FitTrackDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier, containerColor = FitTrackColors.Surface) {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = destination.id == selectedId,
                onClick = { onSelect(destination.id) },
                icon = destination.icon,
                label = { Text(destination.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FitTrackColors.OnPrimary,
                    selectedTextColor = FitTrackColors.TextPrimary,
                    indicatorColor = FitTrackColors.Primary,
                    unselectedIconColor = FitTrackColors.TextSecondary,
                    unselectedTextColor = FitTrackColors.TextSecondary
                )
            )
        }
    }
}
