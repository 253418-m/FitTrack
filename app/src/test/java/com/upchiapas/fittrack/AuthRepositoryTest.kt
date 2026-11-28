@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.upchiapas.fittrack

import com.upchiapas.fittrack.core.network.*
import com.upchiapas.fittrack.core.session.*
import com.upchiapas.fittrack.features.auth.data.*
import com.upchiapas.fittrack.features.auth.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class AuthRepositoryTest {
    private val user = UserDto("00000000-0000-4000-8000-000000000001", "Ana", "ana", "ana@example.com", "ATHLETE", "2026-11-27T00:00:00Z")
    private inner class Api : AuthApi {
        var response = SessionDto("test-token", "bearer", 60, user)
        var gate: CompletableDeferred<Unit>? = null
        var failure: AuthError? = null
        override suspend fun login(body: LoginDto): SessionDto { gate?.await(); return response }
        override suspend fun register(body: RegisterDto) = response
        override suspend fun me(): UserDto { failure?.let { throw AuthFailure(it) }; return user }
    }
    private class Store : SessionStore {
        var saved: StoredSession? = null
        var failWrite = false
        var failClear = false
        override suspend fun read() = saved
        override suspend fun write(session: StoredSession) { if (failWrite) error("storage"); saved = session }
        override suspend fun clear() { saved = null; if (failClear) error("storage") }
    }
    @Test fun loginAndExpiry() = runTest {
        val snapshot = SessionSnapshot()
        val repo = AuthRepositoryImpl(Api(), Store(), snapshot, Json, backgroundScope) { testScheduler.currentTime }
        repo.login(LoginInput("ana@example.com", "exact"))
        assertTrue(repo.state.value is SessionState.Authenticated)
        advanceTimeBy(60001); runCurrent()
        assertTrue(repo.state.value is SessionState.SignedOut)
        assertNull(snapshot.current)
    }
    @Test fun restoresOnlyAfterMeAndRetainsSessionOnNetworkFailure() = runTest {
        val api = Api().apply { failure = AuthError.Offline }
        val store = Store().apply { saved = StoredSession("token", 60000) }
        val repo = AuthRepositoryImpl(api, store, SessionSnapshot(), Json, backgroundScope) { testScheduler.currentTime }
        repo.restore()
        assertEquals(SessionState.Recovery(AuthError.Offline), repo.state.value)
        assertNotNull(store.saved)
        api.failure = null
        repo.restore()
        assertTrue(repo.state.value is SessionState.Authenticated)
    }
    @Test fun protected401ClearsBut403DoesNot() = runTest {
        for (error in listOf(AuthError.Expired, AuthError.Forbidden)) {
            val store = Store().apply { saved = StoredSession("token", 60000) }
            val repo = AuthRepositoryImpl(Api().apply { failure = error }, store, SessionSnapshot(), Json, backgroundScope) { 0 }
            repo.restore()
            if (error == AuthError.Expired) assertNull(store.saved) else assertNotNull(store.saved)
        }
    }
    @Test fun logoutOfflineRejectsLateLogin() = runTest {
        val api = Api().apply { gate = CompletableDeferred() }
        val store = Store()
        val repo = AuthRepositoryImpl(api, store, SessionSnapshot(), Json, backgroundScope) { 0 }
        val login = launch { repo.login(LoginInput("a@example.com", "password")) }
        runCurrent()
        repo.logout()
        api.gate!!.complete(Unit); login.join()
        assertTrue(repo.state.value is SessionState.SignedOut)
        assertNull(store.saved)
    }
    @Test fun old401CannotClearNewSession() = runTest {
        val snapshot = SessionSnapshot()
        val repo = AuthRepositoryImpl(Api(), Store(), snapshot, Json, backgroundScope) { 0 }
        repo.login(LoginInput("a@example.com", "password"))
        val old = snapshot.current!!.generation
        repo.logout()
        repo.login(LoginInput("a@example.com", "password"))
        snapshot.unauthorized(old); runCurrent()
        assertTrue(repo.state.value is SessionState.Authenticated)
    }
    @Test fun storageFailureNeverGrantsAccess() = runTest {
        val store = Store().apply { failWrite = true }
        val repo = AuthRepositoryImpl(Api(), store, SessionSnapshot(), Json, backgroundScope) { 0 }
        try { repo.login(LoginInput("a@example.com", "password")); fail() } catch (e: AuthFailure) { assertEquals(AuthError.Storage, e.reason) }
        assertFalse(repo.state.value is SessionState.Authenticated)
        store.failClear = true
        repo.logout()
        assertEquals(SessionState.Recovery(AuthError.Storage), repo.state.value)
    }
    @Test fun rejectsInvalidTokenOrRole() = runTest {
        val api = Api()
        val repo = AuthRepositoryImpl(api, Store(), SessionSnapshot(), Json, backgroundScope) { 0 }
        for (response in listOf(api.response.copy(access_token = ""), api.response.copy(user = user.copy(role = "ADMIN")), api.response.copy(expires_in = 0))) {
            api.response = response
            try { repo.login(LoginInput("a@example.com", "password")); fail() } catch (e: AuthFailure) { assertEquals(AuthError.InvalidResponse, e.reason) }
        }
    }

    @Test fun expiredStoredSessionNeverCallsMe() = runTest {
        val store = Store().apply { saved = StoredSession("token", 1) }
        val repo = AuthRepositoryImpl(Api().apply { failure = AuthError.Unexpected }, store, SessionSnapshot(), Json, backgroundScope) { 2 }
        repo.restore()
        assertEquals(SessionState.SignedOut(AuthError.Expired), repo.state.value)
        assertNull(store.saved)
    }
    @Test fun readFailureIsClearedAndBlocked() = runTest {
        val store = object : SessionStore {
            override suspend fun read(): StoredSession? = error("damaged ciphertext")
            override suspend fun write(session: StoredSession) = Unit
            override suspend fun clear() = Unit
        }
        val repo = AuthRepositoryImpl(Api(), store, SessionSnapshot(), Json, backgroundScope) { 0 }
        repo.restore()
        assertEquals(SessionState.SignedOut(AuthError.Storage), repo.state.value)
    }
    @Test fun cleanupFailureCannotBeRestored() = runTest {
        val store = Store().apply { failClear = true; saved = StoredSession("token", 60000) }
        val repo = AuthRepositoryImpl(Api(), store, SessionSnapshot(), Json, backgroundScope) { 0 }
        repo.logout()
        store.saved = StoredSession("residual", 60000)
        repo.restore()
        assertEquals(SessionState.Recovery(AuthError.Storage), repo.state.value)
    }
}
