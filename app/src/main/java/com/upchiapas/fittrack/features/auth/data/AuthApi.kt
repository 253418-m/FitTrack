package com.upchiapas.fittrack.features.auth.data

import com.upchiapas.fittrack.features.auth.domain.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import retrofit2.http.*
import java.time.Instant
import java.util.UUID

@Serializable data class LoginDto(val email: String, val password: String)
@Serializable data class RegisterDto(val name: String, val username: String, val email: String, val password: String, val password_confirmation: String, val invitation_code: String)
@Serializable data class UserDto(val id: String, val name: String, val username: String, val email: String, val role: String, val created_at: String) {
    fun toDomain(): User = try {
        require(UUID.fromString(id).toString() == id.lowercase())
        require(name.isNotBlank() && AuthValidation.validEmail(email))
        require(Regex("^[a-z0-9._]{3,30}$").matches(username))
        Instant.parse(created_at)
        User(id, name, username, email, Role.valueOf(role))
    } catch (_: Exception) { throw AuthFailure(AuthError.InvalidResponse) }
}
@Serializable data class SessionDto(val access_token: String, val token_type: String, val expires_in: Long, val user: UserDto)
@Serializable data class ErrorBody(val error: ErrorDto)
@Serializable data class ErrorDto(val code: String, val message: String = "", val details: List<kotlinx.serialization.json.JsonElement> = emptyList())
interface AuthApi {
    @POST("api/v1/auth/login") suspend fun login(@Body body: LoginDto): SessionDto
    @POST("api/v1/auth/register") suspend fun register(@Body body: RegisterDto): SessionDto
    @GET("api/v1/users/me") suspend fun me(): UserDto
}
