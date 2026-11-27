package com.upchiapas.fittrack.core.designsystem.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.upchiapas.fittrack.core.designsystem.components.FitTrackDialog
import com.upchiapas.fittrack.core.designsystem.components.FitTrackState
import com.upchiapas.fittrack.core.designsystem.components.FitTrackStateContent
import com.upchiapas.fittrack.core.designsystem.components.FitTrackTopBar
import com.upchiapas.fittrack.ui.theme.FitTrackTheme

@Preview(showBackground = true, widthDp = 390)
@Composable
fun TopBarPreview() {
    FitTrackTheme {
        Surface {
            FitTrackTopBar("Rutinas", onBack = {}, backIcon = { Text("‹") })
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
fun StateVariantsPreview() {
    FitTrackTheme {
        Surface {
            Column {
                FitTrackStateContent(FitTrackState.Loading, "Cargando", "Espera un momento.")
                FitTrackStateContent(
                    FitTrackState.Error,
                    "No fue posible cargar",
                    "Comprueba tu conexión.",
                    actionLabel = "Reintentar",
                    onAction = {},
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
fun DestructiveDialogPreview() {
    FitTrackTheme {
        Surface {
            FitTrackDialog(
                "Eliminar rutina",
                "¿Deseas continuar?",
                "Eliminar",
                "Cancelar",
                {},
                {},
                destructive = true
            )
        }
    }
}
