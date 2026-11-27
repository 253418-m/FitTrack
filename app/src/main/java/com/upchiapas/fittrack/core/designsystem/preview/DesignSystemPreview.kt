package com.upchiapas.fittrack.core.designsystem.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upchiapas.fittrack.core.designsystem.components.*
import com.upchiapas.fittrack.ui.theme.FitTrackSpacing
import com.upchiapas.fittrack.ui.theme.FitTrackTheme

@Preview(showBackground = true, widthDp = 390)
@Composable
fun DesignSystemPreview() {
    FitTrackTheme {
      Surface {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(FitTrackSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(FitTrackSpacing.Small)
        ) {
            FitTrackBrandMark()
            Text("FitTrack", style = MaterialTheme.typography.headlineLarge)
            Text("Tus rutinas y hábitos, sin ruido.", style = MaterialTheme.typography.bodyMedium)
            FitTrackButton("Continuar", {})
            FitTrackButton("Acción secundaria", {}, variant = FitTrackButtonVariant.Secondary)
            FitTrackButton("Eliminar", {}, variant = FitTrackButtonVariant.Destructive)
            FitTrackButton("Cargando", {}, loading = true)
            FitTrackButton("Deshabilitado", {}, enabled = false)
            FitTrackTextField("Correo", "", {}, placeholder = "nombre@ejemplo.com")
            FitTrackTextField("Campo con error", "", {}, isError = true, supportingText = "Revisa este dato")
            FitTrackPasswordField("Contraseña", "", {})
            FitTrackCard { Text("Tarjeta de ejemplo", modifier = Modifier.padding(16.dp)) }
            FitTrackChip("Pendiente", variant = FitTrackChipVariant.Warning)
            FitTrackAvatar("FT")
            FitTrackProgress(0.6f, label = "Progreso")
            Switch(checked = true, onCheckedChange = {})
            Checkbox(checked = true, onCheckedChange = {})
            FitTrackStateContent(FitTrackState.Empty, "Sin contenido", "Aún no hay elementos.")
        }
      }
    }
}

private fun destinations(vararg labels: String) = labels.map {
    FitTrackDestination(it, it) { Text(it.first().toString()) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
fun AthleteBottomBarPreview() {
    FitTrackTheme {
      Surface {
        FitTrackBottomBar(
            destinations = destinations("Hoy", "Calculadora", "Rutinas", "Entrenador"),
            selectedId = "Hoy",
            onSelect = {}
        )
      }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
fun TrainerBottomBarPreview() {
    FitTrackTheme {
      Surface {
        FitTrackBottomBar(
            destinations = destinations("Rutinas", "Atletas", "Calculadora"),
            selectedId = "Rutinas",
            onSelect = {}
        )
      }
    }
}

@Preview(showBackground = true, widthDp = 320, fontScale = 1.3f)
@Composable
fun CompactTextPreview() {
    FitTrackTheme {
      Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            FitTrackButton("Confirmar una acción con etiqueta larga", {})
            FitTrackStateContent(FitTrackState.Error, "No fue posible completar la acción", "Comprueba tu conexión.", actionLabel = "Reintentar", onAction = {})
        }
      }
    }
}
