package com.upchiapas.fittrack

import com.upchiapas.fittrack.core.network.*
import com.upchiapas.fittrack.features.auth.data.*
import com.upchiapas.fittrack.features.auth.domain.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.mockwebserver.*
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AuthNetworkTest {
    private val user = """{"id":"00000000-0000-4000-8000-000000000001","name":"Ana","username":"ana","email":"ana@example.com","role":"TRAINER","created_at":"2026-11-27T00:00:00Z"}"""
    @Test fun routesJsonAndProtectedBearer() = runTest {
        MockWebServer().use { server ->
            server.start()
            val snapshot = SessionSnapshot().apply { current = TokenSnapshot("fake-token", Long.MAX_VALUE, 1) }
            val client = OkHttpClient.Builder().addInterceptor(AuthInterceptor(server.url("/"), snapshot)).build()
            val api = Retrofit.Builder().baseUrl(server.url("/")).client(client).addConverterFactory(Json.asConverterFactory("application/json".toMediaType())).build().create(AuthApi::class.java)
            server.enqueue(MockResponse().setBody("""{"access_token":"fake-token","token_type":"bearer","expires_in":120,"user":$user}"""))
            val result = api.login(LoginDto("ana@example.com", " exact "))
            assertEquals(Role.TRAINER, result.user.toDomain().role)
            val login = server.takeRequest()
            assertEquals("/api/v1/auth/login", login.path); assertNull(login.getHeader("Authorization"))
            assertEquals(" exact ", Json.parseToJsonElement(login.body.readUtf8()).let { (it as kotlinx.serialization.json.JsonObject)["password"].toString().trim('"') })
            server.enqueue(MockResponse().setBody(user))
            api.me()
            val me = server.takeRequest()
            assertEquals("/api/v1/users/me", me.path); assertEquals("Bearer fake-token", me.getHeader("Authorization"))
            server.enqueue(MockResponse().setResponseCode(201).setBody("""{"access_token":"fake-token","token_type":"bearer","expires_in":120,"user":$user}"""))
            api.register(RegisterDto("Ana","ana","ana@example.com","password","password","test-invitation-code"))
            val register = server.takeRequest()
            assertEquals("/api/v1/auth/register", register.path); assertNull(register.getHeader("Authorization"))
            val jsonBody = Json.parseToJsonElement(register.body.readUtf8()) as kotlinx.serialization.json.JsonObject
            assertTrue(jsonBody.containsKey("invitation_code")); assertFalse(jsonBody.containsKey("role"))
        }
    }
    @Test fun mapsHttpErrorsWithoutExposingServerMessage() = runTest {
        MockWebServer().use { server ->
            server.start()
            val api = Retrofit.Builder().baseUrl(server.url("/")).addConverterFactory(Json.asConverterFactory("application/json".toMediaType())).build().create(AuthApi::class.java)
            for ((status, code, expected) in listOf(Triple(400,"INVALID_INVITATION",AuthError.InvalidInvitation),Triple(409,"GYM_CAPACITY_REACHED",AuthError.CapacityReached),Triple(401,"INVALID_CREDENTIALS",AuthError.InvalidCredentials),Triple(409,"USERNAME_CONFLICT",AuthError.UsernameConflict),Triple(409,"EMAIL_CONFLICT",AuthError.EmailConflict),Triple(422,"VALIDATION_ERROR",AuthError.Validation),Triple(403,"FORBIDDEN",AuthError.Forbidden))) {
                server.enqueue(MockResponse().setResponseCode(status).setBody("""{"error":{"code":"$code","message":"private detail","details":[]}}"""))
                try { safeApi(Json, login = true) { api.login(LoginDto("a@example.com","password")) }; fail() }
                catch(e: AuthFailure) { assertEquals(expected,e.reason); assertFalse(e.message.orEmpty().contains("private detail")) }
            }
        }
    }
    @Test fun neverSendsTokenToOtherOriginOrExpiredSession() {
        MockWebServer().use { server -> MockWebServer().use { other ->
            server.start(); other.start()
            val snapshot = SessionSnapshot().apply { current = TokenSnapshot("fake-token", 100, 1) }
            val client = OkHttpClient.Builder().addInterceptor(AuthInterceptor(server.url("/"),snapshot, now = { 0 })).build()
            other.enqueue(MockResponse())
            client.newCall(Request.Builder().url(other.url("/api/v1/users/me")).build()).execute().close()
            assertNull(other.takeRequest().getHeader("Authorization"))
            val expired = OkHttpClient.Builder().addInterceptor(AuthInterceptor(server.url("/"),snapshot, now = { 200 })).build()
            server.enqueue(MockResponse())
            expired.newCall(Request.Builder().url(server.url("/api/v1/users/me")).build()).execute().close()
            assertNull(server.takeRequest().getHeader("Authorization"))
        } }
    }
}
