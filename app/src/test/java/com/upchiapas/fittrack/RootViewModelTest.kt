@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.upchiapas.fittrack

import com.upchiapas.fittrack.features.auth.domain.*
import com.upchiapas.fittrack.features.auth.presentation.RootViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

class RootViewModelTest {
    @Test fun restorationErrorRetriesAndLogoutWorksLocally() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = object : SessionRepository {
                override val state = MutableStateFlow<SessionState>(SessionState.Restoring)
                var calls = 0
                override suspend fun restore() { calls++; state.value = if (calls == 1) SessionState.Recovery(AuthError.Offline) else SessionState.Authenticated(User("id", "Ana", "ana", "ana@example.com", Role.ATHLETE),60000) }
                override suspend fun logout() { state.value = SessionState.SignedOut() }
            }
            val vm = RootViewModel(ObserveSession(fake), RestoreSession(fake), Logout(fake))
            runCurrent()
            assertEquals(SessionState.Recovery(AuthError.Offline), vm.state.value)
            vm.retry(); runCurrent(); assertTrue(vm.state.value is SessionState.Authenticated)
            vm.signOut(); runCurrent(); assertTrue(vm.state.value is SessionState.SignedOut)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun logoutCancelsPendingRestoration() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = object : SessionRepository {
                override val state = MutableStateFlow<SessionState>(SessionState.Restoring)
                var cancelled = false
                override suspend fun restore() { try { awaitCancellation() } finally { cancelled = true } }
                override suspend fun logout() { state.value = SessionState.SignedOut() }
            }
            val vm = RootViewModel(ObserveSession(fake), RestoreSession(fake), Logout(fake))
            runCurrent(); vm.signOut(); runCurrent()
            assertTrue(fake.cancelled); assertTrue(vm.state.value is SessionState.SignedOut)
        } finally { Dispatchers.resetMain() }
    }
}
