@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.upchiapas.fittrack

import com.upchiapas.fittrack.features.auth.domain.*
import com.upchiapas.fittrack.features.auth.presentation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

class AuthViewModelTest {
    private class Fake : AuthRepository {
        var calls = 0
        var gate = CompletableDeferred<Unit>()
        var error: AuthError? = null
        override suspend fun login(input: LoginInput) { calls++; gate.await(); error?.let { throw AuthFailure(it) } }
        override suspend fun register(input: RegisterInput) = login(LoginInput(input.email, input.password))
    }
    @Test fun loadingPreventsDoubleSubmitAndClearCancels() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = Fake()
            val vm = AuthViewModel(LoginUseCase(fake), RegisterUseCase(fake))
            vm.editLogin(LoginInput("a@example.com", "password"))
            vm.submit(false); vm.submit(false); runCurrent()
            assertEquals(1, fake.calls); assertTrue(vm.state.value.loading)
            vm.clear(); fake.gate.complete(Unit); runCurrent()
            assertEquals(AuthUiState(), vm.state.value)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun typedErrorsArePresented() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            for (error in listOf(AuthError.InvalidCredentials, AuthError.UsernameConflict, AuthError.EmailConflict, AuthError.Validation)) {
                val fake = Fake().apply { this.error = error; gate.complete(Unit) }
                val vm = AuthViewModel(LoginUseCase(fake), RegisterUseCase(fake))
                vm.editLogin(LoginInput("a@example.com", "password")); vm.submit(false); runCurrent()
                assertEquals(error, vm.state.value.error); assertFalse(vm.state.value.loading)
                vm.clear()
            }
        } finally { Dispatchers.resetMain() }
    }
}
