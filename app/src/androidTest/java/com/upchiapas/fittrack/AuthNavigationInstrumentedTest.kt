package com.upchiapas.fittrack

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.lifecycle.viewmodel.initializer
import com.upchiapas.fittrack.core.presentation.FitTrackApp
import com.upchiapas.fittrack.features.auth.domain.*
import com.upchiapas.fittrack.features.auth.presentation.RootViewModel
import com.upchiapas.fittrack.features.auth.presentation.AuthViewModel
import com.upchiapas.fittrack.ui.theme.FitTrackTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthNavigationInstrumentedTest {
    @get:Rule val compose = createComposeRule()
    private class Fake : SessionRepository {
        override val state = MutableStateFlow<SessionState>(SessionState.Restoring)
        override suspend fun restore() { state.value = SessionState.Authenticated(User("id", "Ana", "ana", "ana@example.com", Role.ATHLETE), Long.MAX_VALUE) }
        override suspend fun logout() { state.value = SessionState.SignedOut() }
    }
    @Test fun logoutRemovesPrivateGraph() {
        val fake = Fake()
        val auth = object : AuthRepository {
            override suspend fun login(input: LoginInput) = Unit
            override suspend fun register(input: RegisterInput) = Unit
        }
        val factory = androidx.lifecycle.viewmodel.viewModelFactory {
            initializer { RootViewModel(ObserveSession(fake), RestoreSession(fake), Logout(fake)) }
            initializer { AuthViewModel(LoginUseCase(auth), RegisterUseCase(auth)) }
        }
        compose.setContent { FitTrackTheme {
            FitTrackApp(androidx.lifecycle.viewmodel.compose.viewModel(factory = factory),
                authViewModel = { androidx.lifecycle.viewmodel.compose.viewModel(factory = factory) })
        } }
        compose.onNodeWithText("Hoy").assertIsDisplayed()
        compose.onNodeWithText("Configuración").performClick()
        compose.onNodeWithText("Cerrar sesión").performClick()
        compose.onNodeWithText("¿Quieres cerrar tu sesión en este dispositivo?").assertIsDisplayed()
        compose.onAllNodesWithText("Cerrar sesión").onLast().performClick()
        compose.waitUntil { fake.state.value is SessionState.SignedOut }
        compose.onNodeWithText("Iniciar sesión").assertExists()
        compose.onNodeWithText("Hoy").assertDoesNotExist()
    }
}
