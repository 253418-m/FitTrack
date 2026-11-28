package com.upchiapas.fittrack.features.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.upchiapas.fittrack.features.auth.domain.*
import com.upchiapas.fittrack.core.designsystem.components.*
import com.upchiapas.fittrack.ui.theme.FitTrackTheme

fun AuthError.message(): String = when(this) {
    AuthError.InvalidInvitation -> "Invitación no válida. Solicita ayuda al gimnasio."
    AuthError.CapacityReached -> "No hay cupo disponible para completar el registro."
    AuthError.InvalidCredentials -> "Correo o contraseña incorrectos."
    AuthError.Expired -> "Tu sesión ha expirado. Conéctate a Internet e inicia sesión para continuar."
    AuthError.UsernameConflict -> "Este nombre de usuario ya está registrado."
    AuthError.EmailConflict -> "Este correo ya está registrado."
    AuthError.Validation -> "Revisa los campos: correo válido y contraseñas coincidentes dentro de los límites indicados."
    AuthError.Forbidden -> "No tienes permiso para realizar esta operación."
    AuthError.Offline -> "No se pudo conectar con el servicio. Comprueba tu conexión y vuelve a intentarlo."
    AuthError.Timeout -> "El servicio tardó demasiado. Vuelve a intentarlo."
    AuthError.Unavailable -> "Servicio temporalmente no disponible."
    AuthError.InvalidResponse -> "La respuesta del servicio no es válida."
    AuthError.Storage -> "No se pudo proteger o eliminar la sesión. El acceso está bloqueado."
    AuthError.Unexpected -> "No se pudo completar la operación."
}
@Composable
fun AuthScreen(state: AuthUiState, registration: Boolean, onLogin: (LoginInput) -> Unit, onRegister: (RegisterInput) -> Unit, onSubmit: () -> Unit, onSwitch: () -> Unit, sessionError: AuthError? = null) {
    Column(Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FitTrackBrandMark()
        Text("FitTrack Gym", style = MaterialTheme.typography.headlineLarge)
        Text(if (registration) "Crear cuenta" else "Tus rutinas y hábitos, sin ruido.", style = MaterialTheme.typography.titleMedium)
        val r = state.register
        val touched = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<String, Boolean>() }
        val errors = AuthValidation.registrationErrors(r)
        fun fieldError(key: String) = if (touched[key] == true) errors[key] else null
        if (registration) {
            // Inline feedback uses the same rules as the ViewModel and use case.
            val values = listOf("invitationCode" to r.invitationCode, "name" to r.name, "username" to r.username, "email" to r.email, "password" to r.password, "confirmation" to r.confirmation)
            androidx.compose.runtime.LaunchedEffect(r) { values.filter { it.second.isNotEmpty() }.forEach { touched[it.first] = true } }
            values.forEach { (key, _) -> fieldError(key)?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
        }
        if (registration) {
            FitTrackTextField("Código de invitación", r.invitationCode, { onRegister(r.copy(invitationCode = it)) }, enabled = !state.loading, supportingText = "Código individual proporcionado por FitTrack Gym")
            FitTrackTextField("Nombre completo", r.name, { onRegister(r.copy(name = it)) }, enabled = !state.loading, supportingText = "De 1 a 100 caracteres")
            FitTrackTextField("Nombre de usuario", r.username, { onRegister(r.copy(username = it)) }, enabled = !state.loading, supportingText = "3–30: letras ASCII, números, punto y guion bajo")
        }
        FitTrackTextField("Correo electrónico", if (registration) r.email else state.login.email,
            { if (registration) onRegister(r.copy(email = it)) else onLogin(state.login.copy(email = it)) },
            enabled = !state.loading, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        FitTrackPasswordField("Contraseña", if (registration) r.password else state.login.password,
            { if (registration) onRegister(r.copy(password = it)) else onLogin(state.login.copy(password = it)) },
            enabled = !state.loading, supportingText = if (registration) "De 8 a 128 caracteres" else null)
        if (registration) FitTrackPasswordField("Confirmar contraseña", r.confirmation, { onRegister(r.copy(confirmation = it)) }, enabled = !state.loading,
            isError = r.confirmation.isNotEmpty() && r.confirmation != r.password)
        (state.error ?: sessionError)?.let { Text(it.message(), color = MaterialTheme.colorScheme.error) }
        FitTrackButton(if (state.loading) "Verificando credenciales" else if (registration) "Crear cuenta" else "Iniciar sesión",
            onSubmit, Modifier.fillMaxWidth(), enabled = if (registration) AuthValidation.valid(r) else AuthValidation.valid(state.login), loading = state.loading)
        TextButton(onClick = onSwitch, enabled = !state.loading, modifier = Modifier.heightIn(min = 48.dp)) { Text(if (registration) "Ya tengo cuenta: iniciar sesión" else "¿No tienes cuenta? Crear cuenta") }
    }
}
@Composable
fun SessionScreen(restoring: Boolean, error: AuthError? = null, onRetry: () -> Unit = {}, onLogout: () -> Unit = {}) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FitTrackStateContent(if (restoring) FitTrackState.Loading else FitTrackState.Error,
            if (restoring) "Restaurando sesión" else "Sesión sin validar",
            if (restoring) "Verificando tu identidad con el servidor." else error?.message().orEmpty(),
            actionLabel = if (restoring) null else "Reintentar", onAction = onRetry)
        if (!restoring) FitTrackButton("Cerrar sesión", onLogout)
    }
}
@Preview @Composable private fun LoginPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { AuthScreen(AuthUiState(), false, {}, {}, {}, {}) } } }
@Preview @Composable private fun LoginLoadingPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { AuthScreen(AuthUiState(loading = true), false, {}, {}, {}, {}) } } }
@Preview @Composable private fun LoginErrorPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { AuthScreen(AuthUiState(error = AuthError.InvalidCredentials), false, {}, {}, {}, {}) } } }
@Preview @Composable private fun RegisterConflictPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { AuthScreen(AuthUiState(error = AuthError.EmailConflict), true, {}, {}, {}, {}) } } }
@Preview @Composable private fun RegisterValidationPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { AuthScreen(AuthUiState(error = AuthError.Validation), true, {}, {}, {}, {}) } } }
@Preview @Composable private fun RestorePreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { SessionScreen(true) } } }
@Preview @Composable private fun ConnectionPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { SessionScreen(false, AuthError.Offline) } } }
@Preview @Composable private fun ExpiredPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { AuthScreen(AuthUiState(), false, {}, {}, {}, {}, AuthError.Expired) } } }
@Preview @Composable private fun LogoutPreview() { FitTrackTheme { FitTrackDialog("Cerrar sesión", "¿Quieres cerrar tu sesión en este dispositivo?", "Cerrar sesión", "Cancelar", {}, {}) } }

@Preview @Composable private fun GymRegisterPreview() { FitTrackTheme { Surface(color = MaterialTheme.colorScheme.background) { AuthScreen(AuthUiState(), true, {}, {}, {}, {}) } } }
