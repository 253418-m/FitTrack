package com.upchiapas.fittrack.core.network

import com.upchiapas.fittrack.features.auth.data.ErrorBody
import com.upchiapas.fittrack.features.auth.domain.*
import kotlinx.serialization.json.Json
import okhttp3.*
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException

data class TokenSnapshot(val token: String, val expiresAt: Long, val generation: Long)
class SessionSnapshot {
    @Volatile var current: TokenSnapshot? = null
    var unauthorized: (Long) -> Unit = {}
}
class AuthInterceptor(private val origin: HttpUrl, private val session: SessionSnapshot, private val enabled: Boolean = true, private val now: () -> Long = System::currentTimeMillis) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!enabled) throw IOException("Remote service is not configured")
        val request = chain.request()
        val sameOrigin = request.url.scheme == origin.scheme && request.url.host == origin.host && request.url.port == origin.port
        val protected = request.url.encodedPath.startsWith("/api/v1/") && !request.url.encodedPath.startsWith("/api/v1/auth/")
        val snapshot = session.current
        val builder = request.newBuilder().removeHeader("Authorization")
        if (sameOrigin && protected && snapshot != null && now() < snapshot.expiresAt) {
            builder.header("Authorization", "Bearer ${snapshot.token}")
        }
        val response = chain.proceed(builder.build())
        if (sameOrigin && protected && response.code == 401 && snapshot != null) session.unauthorized(snapshot.generation)
        return response
    }
}
suspend fun <T> safeApi(json: Json, login: Boolean = false, block: suspend () -> T): T = try {
    block()
} catch (e: CancellationException) { throw e
} catch (e: AuthFailure) { throw e
} catch (e: HttpException) {
    val code = try { json.decodeFromString<ErrorBody>(e.response()?.errorBody()?.string().orEmpty()).error.code } catch (_: Exception) { "" }
    throw AuthFailure(when (e.code()) {
        400 -> if (code == "INVALID_INVITATION") AuthError.InvalidInvitation else AuthError.Validation
        401 -> if (login) AuthError.InvalidCredentials else AuthError.Expired
        403 -> AuthError.Forbidden
        409 -> when(code) { "GYM_CAPACITY_REACHED" -> AuthError.CapacityReached; "USERNAME_CONFLICT" -> AuthError.UsernameConflict; "EMAIL_CONFLICT" -> AuthError.EmailConflict; else -> AuthError.Validation }
        422 -> AuthError.Validation
        in 500..599 -> AuthError.Unavailable
        else -> AuthError.Unexpected
    })
} catch (_: SocketTimeoutException) { throw AuthFailure(AuthError.Timeout)
} catch (_: IOException) { throw AuthFailure(AuthError.Offline)
} catch (_: kotlinx.serialization.SerializationException) { throw AuthFailure(AuthError.InvalidResponse)
} catch (_: IllegalArgumentException) { throw AuthFailure(AuthError.InvalidResponse)
}
