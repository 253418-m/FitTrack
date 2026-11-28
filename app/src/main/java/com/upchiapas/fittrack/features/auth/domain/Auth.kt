package com.upchiapas.fittrack.features.auth.domain

import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

enum class Role { ATHLETE, TRAINER }
data class User(val id: String, val name: String, val username: String, val email: String, val role: Role)
enum class AuthError { InvalidInvitation, CapacityReached, InvalidCredentials, Expired, UsernameConflict, EmailConflict, Validation, Forbidden, Offline, Timeout, Unavailable, InvalidResponse, Storage, Unexpected }
class AuthFailure(val reason: AuthError) : Exception(reason.name)
data class LoginInput(val email: String = "", val password: String = "")
data class RegisterInput(val name: String = "", val username: String = "", val email: String = "", val password: String = "", val confirmation: String = "", val invitationCode: String = "")
object AuthValidation {
    fun email(value: String) = value.trim().lowercase(Locale.ROOT)
    fun username(value: String) = value.trim().lowercase(Locale.ROOT)
    fun validEmail(value: String) = email(value).let { it.length <= 254 && Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(it) }
    fun valid(input: LoginInput) = validEmail(input.email) && input.password.length in 1..128
    fun registrationErrors(input: RegisterInput): Map<String, String> = buildMap {
        if (input.name.trim().length !in 1..100) put("name", "Introduce un nombre de 1 a 100 caracteres.")
        if (!Regex("^[a-z0-9._]{3,30}$").matches(username(input.username))) put("username", "Usa de 3 a 30 letras ASCII, números, puntos o guiones bajos.")
        if (!validEmail(input.email)) put("email", "Introduce un correo electrónico válido.")
        if (input.password.length !in 8..128) put("password", "La contraseña debe tener de 8 a 128 caracteres.")
        if (input.password != input.confirmation) put("confirmation", "Las contraseñas no coinciden.")
        if (input.invitationCode.length !in 16..128 || input.invitationCode.any { it.isWhitespace() }) put("invitationCode", "Introduce el código completo, de 16 a 128 caracteres y sin espacios.")
    }
    fun valid(input: RegisterInput) = registrationErrors(input).isEmpty()
}
sealed interface SessionState {
    data object Restoring : SessionState
    data class SignedOut(val error: AuthError? = null) : SessionState
    data class Authenticated(val user: User, val expiresAt: Long) : SessionState
    data class Recovery(val error: AuthError) : SessionState
}
interface SessionRepository {
    val state: StateFlow<SessionState>
    suspend fun restore()
    suspend fun logout()
}
interface AuthRepository {
    suspend fun login(input: LoginInput)
    suspend fun register(input: RegisterInput)
}
class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(input: LoginInput) {
        if (!AuthValidation.valid(input)) throw AuthFailure(AuthError.Validation)
        repository.login(input.copy(email = AuthValidation.email(input.email)))
    }
}
class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(input: RegisterInput) {
        if (!AuthValidation.valid(input)) throw AuthFailure(AuthError.Validation)
        repository.register(input.copy(name = input.name.trim(), username = AuthValidation.username(input.username), email = AuthValidation.email(input.email)))
    }
}
class ObserveSession(private val repository: SessionRepository) { operator fun invoke() = repository.state }
class RestoreSession(private val repository: SessionRepository) { suspend operator fun invoke() = repository.restore() }
class Logout(private val repository: SessionRepository) { suspend operator fun invoke() = repository.logout() }
