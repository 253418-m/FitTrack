package com.upchiapas.fittrack.features.auth.data

import com.upchiapas.fittrack.core.network.*
import com.upchiapas.fittrack.core.session.*
import com.upchiapas.fittrack.features.auth.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val store: SessionStore,
    private val snapshot: SessionSnapshot,
    private val json: Json,
    private val scope: CoroutineScope,
    private val now: () -> Long = System::currentTimeMillis
) : AuthRepository, SessionRepository {
    private val mutex = Mutex()
    private var generation = 0L
    private var cleanupRequired = false
    private var expiry: Job? = null
    private val mutable = MutableStateFlow<SessionState>(SessionState.Restoring)
    override val state: StateFlow<SessionState> = mutable.asStateFlow()
    init { snapshot.unauthorized = { id -> scope.launch { mutex.withLock { if (id == generation) clear(AuthError.Expired) } } } }
    private suspend fun clear(reason: AuthError? = null) {
        generation++
        snapshot.current = null
        expiry?.cancel()
        mutable.value = SessionState.SignedOut(reason)
        withContext(NonCancellable) {
            try { store.clear(); cleanupRequired = false } catch (_: Exception) { cleanupRequired = true; mutable.value = SessionState.Recovery(AuthError.Storage) }
        }
    }
    override suspend fun logout() { mutex.withLock { clear() } }
    private fun schedule(at: Long, id: Long) {
        expiry?.cancel()
        expiry = scope.launch {
            delay((at - now()).coerceAtLeast(0))
            mutex.withLock { if (generation == id) clear(AuthError.Expired) }
        }
    }
    override suspend fun restore() {
        mutex.withLock { if (cleanupRequired) { clear(AuthError.Storage); return } }
        val id = mutex.withLock { generation++; snapshot.current = null; mutable.value = SessionState.Restoring; generation }
        try {
            val saved = mutex.withLock {
                if (id != generation) return
                try { store.read() } catch (e: CancellationException) { throw e } catch (_: Exception) { clear(AuthError.Storage); return }
            }
            if (saved == null || saved.expiresAt <= now()) { mutex.withLock { if (id == generation) clear(if (saved == null) null else AuthError.Expired) }; return }
            mutex.withLock { if (id != generation) return; snapshot.current = TokenSnapshot(saved.token, saved.expiresAt, id); schedule(saved.expiresAt, id) }
            val user = safeApi(json) { api.me().toDomain() }
            mutex.withLock {
                if (id != generation) return
                if (saved.expiresAt <= now()) clear(AuthError.Expired)
                else mutable.value = SessionState.Authenticated(user, saved.expiresAt)
            }
        } catch (e: CancellationException) { throw e
        } catch (e: AuthFailure) {
            mutex.withLock { if (id == generation) {
                snapshot.current = null
                if (e.reason == AuthError.Expired || e.reason == AuthError.InvalidResponse) clear(e.reason)
                else mutable.value = SessionState.Recovery(e.reason)
            } }
        }
    }
    override suspend fun login(input: LoginInput) = authenticate { api.login(LoginDto(input.email, input.password)) }
    override suspend fun register(input: RegisterInput) = authenticate {
        api.register(RegisterDto(input.name, input.username, input.email, input.password, input.confirmation, input.invitationCode))
    }
    private suspend fun authenticate(call: suspend () -> SessionDto) {
        val id = mutex.withLock { generation }
        val started = now()
        val result = safeApi(json, login = true, block = call)
        val user = result.user.toDomain()
        if (result.access_token.isBlank() || result.access_token.any { it.isWhitespace() } || result.token_type != "bearer" || result.expires_in !in 1..Int.MAX_VALUE.toLong())
            throw AuthFailure(AuthError.InvalidResponse)
        val until = started + result.expires_in * 1000
        mutex.withLock {
            if (id != generation) throw CancellationException("Session changed")
            if (until <= now()) throw AuthFailure(AuthError.Expired)
            try { store.write(StoredSession(result.access_token, until)) }
            catch (e: CancellationException) { withContext(NonCancellable) { clear(AuthError.Storage) }; throw e }
            catch (_: Exception) { clear(AuthError.Storage); throw AuthFailure(AuthError.Storage) }
            generation++
            snapshot.current = TokenSnapshot(result.access_token, until, generation)
            mutable.value = SessionState.Authenticated(user, until)
            schedule(until, generation)
        }
    }
}
