package com.upchiapas.fittrack.core.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.upchiapas.fittrack.core.designsystem.components.*
import com.upchiapas.fittrack.features.auth.domain.*
import com.upchiapas.fittrack.features.auth.presentation.*
import kotlinx.serialization.Serializable

@Serializable data object LoginDestination
@Serializable data object RegisterDestination
@Serializable data object AthleteHome
@Serializable data object TrainerHome
@Serializable data object SettingsDestination

@Composable
fun FitTrackApp(root: RootViewModel = hiltViewModel(), authViewModel: @Composable () -> AuthViewModel = { hiltViewModel() }) {
    val session by root.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize().safeDrawingPadding(), color = MaterialTheme.colorScheme.background) {
        when (val current = session) {
            SessionState.Restoring -> SessionScreen(true)
            is SessionState.Recovery -> SessionScreen(false, current.error, root::retry, root::signOut)
            is SessionState.SignedOut -> key("public") { PublicNavigation(current.error, authViewModel) }
            is SessionState.Authenticated -> key(current.user.id, current.expiresAt) { PrivateNavigation(current.user, root::signOut) }
        }
    }
}
@Composable
private fun PublicNavigation(error: AuthError?, authViewModel: @Composable () -> AuthViewModel) {
    val nav = rememberNavController()
    NavHost(nav, LoginDestination) {
        composable<LoginDestination> {
            val vm = authViewModel()
            val state by vm.state.collectAsStateWithLifecycle()
            DisposableEffect(Unit) { onDispose { vm.clear() } }
            AuthScreen(state, false, vm::editLogin, vm::editRegister, { vm.submit(false) }, { vm.clear(); nav.navigate(RegisterDestination) }, error)
        }
        composable<RegisterDestination> {
            val vm = authViewModel()
            val state by vm.state.collectAsStateWithLifecycle()
            DisposableEffect(Unit) { onDispose { vm.clear() } }
            AuthScreen(state, true, vm::editLogin, vm::editRegister, { vm.submit(true) }, { vm.clear(); nav.popBackStack() })
        }
    }
}
@Composable
private fun PrivateNavigation(user: User, onLogout: () -> Unit) {
    val nav = rememberNavController()
    var confirm by remember { mutableStateOf(false) }
    NavHost(nav, if (user.role == Role.ATHLETE) AthleteHome else TrainerHome) {
        if (user.role == Role.ATHLETE) composable<AthleteHome> {
            HomeContent("Hoy", user, "El seguimiento de hábitos estará disponible en una próxima etapa.", { nav.navigate(SettingsDestination) })
        }
        if (user.role == Role.TRAINER) composable<TrainerHome> {
            HomeContent("Mis rutinas", user, "La gestión de rutinas estará disponible en una próxima etapa.", { nav.navigate(SettingsDestination) })
        }
        composable<SettingsDestination> {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Configuración", style = MaterialTheme.typography.headlineMedium)
                Text(user.name)
                Text("@${user.username}")
                Text("Rol de la cuenta: ${if (user.role == Role.ATHLETE) "Atleta" else "Entrenador"}")
                FitTrackButton("Volver", { nav.popBackStack() })
                FitTrackButton("Cerrar sesión", { confirm = true })
            }
        }
    }
    if (confirm) FitTrackDialog("Cerrar sesión", "¿Quieres cerrar tu sesión en este dispositivo?", "Cerrar sesión", "Cancelar",
        { confirm = false; onLogout() }, { confirm = false }, destructive = true)
}
@Composable private fun HomeContent(title: String, user: User, description: String, settings: () -> Unit) {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FitTrackBrandMark()
        Text(title, style = MaterialTheme.typography.headlineLarge)
        Text(user.name)
        Text(description)
        FitTrackButton("Configuración", settings)
    }
}
