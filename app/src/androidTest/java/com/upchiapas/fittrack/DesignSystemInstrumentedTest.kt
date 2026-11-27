package com.upchiapas.fittrack

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.platform.testTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.upchiapas.fittrack.core.designsystem.components.FitTrackBottomBar
import com.upchiapas.fittrack.core.designsystem.components.FitTrackButton
import com.upchiapas.fittrack.core.designsystem.components.FitTrackDestination
import com.upchiapas.fittrack.core.designsystem.components.FitTrackDialog
import com.upchiapas.fittrack.core.designsystem.components.FitTrackPasswordField
import com.upchiapas.fittrack.core.designsystem.components.FitTrackProgress
import com.upchiapas.fittrack.core.designsystem.components.FitTrackState
import com.upchiapas.fittrack.core.designsystem.components.FitTrackStateContent
import com.upchiapas.fittrack.core.designsystem.components.FitTrackTextField
import com.upchiapas.fittrack.ui.theme.FitTrackTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DesignSystemInstrumentedTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun enabledButtonExecutesCallback() {
        var clicks = 0
        compose.setContent {
            FitTrackTheme { FitTrackButton("Activo", { clicks++ }) }
        }

        compose.onNodeWithText("Activo").performClick()

        compose.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun disabledButtonBlocksCallback() {
        var clicks = 0
        compose.setContent {
            FitTrackTheme { FitTrackButton("Deshabilitado", { clicks++ }, enabled = false) }
        }

        compose.onNodeWithText("Deshabilitado").assertIsNotEnabled()
            .performTouchInput { click() }
        compose.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun loadingButtonBlocksCallback() {
        var clicks = 0
        compose.setContent {
            FitTrackTheme { FitTrackButton("Enviando", { clicks++ }, loading = true) }
        }

        compose.onNodeWithText("Enviando").assertIsNotEnabled()
            .performTouchInput { click() }
        compose.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun textFieldReportsChanges() {
        var value by mutableStateOf("")
        compose.setContent {
            FitTrackTheme {
                FitTrackTextField(
                    label = "Nombre",
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.testTag("name-field")
                )
            }
        }

        compose.onNodeWithTag("name-field").performTextInput("Ana")

        compose.runOnIdle { assertEquals("Ana", value) }
    }

    @Test
    fun errorFieldShowsSupportingMessage() {
        compose.setContent {
            FitTrackTheme {
                FitTrackTextField(
                    label = "Correo",
                    value = "",
                    onValueChange = {},
                    isError = true,
                    supportingText = "Correo no válido"
                )
            }
        }

        compose.onNodeWithText("Correo no válido").assertExists()
    }

    @Test
    fun passwordFieldTogglesVisibilityActionWithoutChangingValue() {
        val password = "secreto"
        compose.setContent {
            FitTrackTheme {
                FitTrackPasswordField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = {},
                    modifier = Modifier.testTag("password-field")
                )
            }
        }

        compose.onNodeWithContentDescription("Mostrar contraseña").assertExists()
        assertEquals(
            "•".repeat(password.length),
            compose.onNodeWithTag("password-field").fetchSemanticsNode()
                .config.getOrNull(SemanticsProperties.EditableText)?.text
        )

        compose.onNodeWithContentDescription("Mostrar contraseña").performClick()
        compose.onNodeWithContentDescription("Ocultar contraseña").assertExists()
        compose.onNodeWithTag("password-field").assertTextContains(password)

        compose.onNodeWithContentDescription("Ocultar contraseña").performClick()
        compose.onNodeWithContentDescription("Mostrar contraseña").assertExists()
        assertEquals(
            "•".repeat(password.length),
            compose.onNodeWithTag("password-field").fetchSemanticsNode()
                .config.getOrNull(SemanticsProperties.EditableText)?.text
        )
    }

    @Test
    fun bottomBarExposesSelectionAndReportsDestination() {
        var selected = ""
        compose.setContent {
            FitTrackTheme {
                FitTrackBottomBar(
                    destinations = listOf(
                        FitTrackDestination("hoy", "Hoy") { Text("H") },
                        FitTrackDestination("rutinas", "Rutinas") { Text("R") }
                    ),
                    selectedId = "hoy",
                    onSelect = { selected = it }
                )
            }
        }

        compose.onNodeWithText("Hoy").assertIsSelected()
        compose.onNodeWithText("Rutinas").performClick()
        compose.runOnIdle { assertEquals("rutinas", selected) }
    }

    @Test
    fun progressExposesNormalizedSemantics() {
        compose.setContent {
            FitTrackTheme {
                FitTrackProgress(progress = 2f, modifier = Modifier.testTag("progress"))
            }
        }

        compose.onNodeWithTag("progress")
            .assertRangeInfoEquals(ProgressBarRangeInfo(1f, 0f..1f))
    }

    @Test
    fun errorStateReportsRetryAction() {
        var retries = 0
        compose.setContent {
            FitTrackTheme {
                FitTrackStateContent(
                    state = FitTrackState.Error,
                    title = "No fue posible cargar",
                    description = "Comprueba tu conexión",
                    actionLabel = "Reintentar",
                    onAction = { retries++ }
                )
            }
        }

        compose.onNodeWithText("Reintentar").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test
    fun dialogReportsConfirmAndDismissActions() {
        var confirmations = 0
        var dismissals = 0
        compose.setContent {
            FitTrackTheme {
                FitTrackDialog(
                    title = "Eliminar rutina",
                    message = "¿Deseas continuar?",
                    confirmLabel = "Eliminar",
                    dismissLabel = "Cancelar",
                    onConfirm = { confirmations++ },
                    onDismissRequest = { dismissals++ }
                )
            }
        }

        compose.onNodeWithText("Eliminar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(1, confirmations)
            assertEquals(1, dismissals)
        }
    }
}
