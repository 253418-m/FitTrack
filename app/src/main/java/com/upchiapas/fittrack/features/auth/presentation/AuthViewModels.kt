package com.upchiapas.fittrack.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upchiapas.fittrack.features.auth.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class AuthUiState(val login: LoginInput = LoginInput(), val register: RegisterInput = RegisterInput(), val loading: Boolean = false, val error: AuthError? = null)
@HiltViewModel
class AuthViewModel @Inject constructor(private val login: LoginUseCase, private val register: RegisterUseCase) : ViewModel() {
    private val mutable = MutableStateFlow(AuthUiState())
    val state = mutable.asStateFlow()
    private var pending: Job? = null
    fun editLogin(input: LoginInput) { if (!mutable.value.loading) mutable.update { it.copy(login = input, error = null) } }
    fun editRegister(input: RegisterInput) { if (!mutable.value.loading) mutable.update { it.copy(register = input, error = null) } }
    fun submit(registration: Boolean) {
        if (mutable.value.loading) return
        val input = mutable.value
        if (if (registration) !AuthValidation.valid(input.register) else !AuthValidation.valid(input.login)) {
            mutable.update { it.copy(error = AuthError.Validation) }; return
        }
        mutable.update { it.copy(loading = true, error = null) }
        pending = viewModelScope.launch {
            try {
                if (registration) register(input.register) else login(input.login)
                mutable.value = AuthUiState()
            } catch (e: CancellationException) { throw e
            } catch (e: AuthFailure) { mutable.update { it.copy(error = e.reason) }
            } catch (_: Exception) { mutable.update { it.copy(error = AuthError.Unexpected) }
            } finally { mutable.update { it.copy(loading = false) } }
        }
    }
    fun clear() { pending?.cancel(); mutable.value = AuthUiState() }
}
@HiltViewModel
class RootViewModel @Inject constructor(observe: ObserveSession, private val restore: RestoreSession, private val logout: Logout) : ViewModel() {
    val state = observe()
    private var operation: Job? = null
    init { retry() }
    fun retry() { if (operation?.isActive == true) return; operation = viewModelScope.launch { restore() } }
    fun signOut() { operation?.cancel(); operation = viewModelScope.launch { logout() } }
}
