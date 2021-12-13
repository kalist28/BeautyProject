package ru.kalistratov.template.beauty.presentation.feature.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import ru.kalistratov.template.beauty.presentation.feature.auth.view.AuthIntent

data class AuthState(
    val login: String? = null,
    val password: String? = null,
    val isLoading: Boolean = false,
    val isAuthFailed: Boolean = false,
) : BaseState

sealed class AuthAction : BaseAction {
    object Clear : AuthAction()
    object AuthFailed : AuthAction()

    data class UpdateLogin(val login: String) : AuthAction()
    data class UpdatePassword(val password: String) : AuthAction()
    data class UpdateShowLoading(val show: Boolean) : AuthAction()
}

class AuthViewModel @Inject constructor(
    private val router: AuthRouter,
    private val interactor: AuthInteractor,
) : BaseViewModel<AuthIntent, AuthAction, AuthState>() {

    private val initialState = AuthState()

    private val initialStateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            intentFlow.filterIsInstance<AuthIntent.RegistrationClick>()
                .onEach { router.openRegistration() }
                .launchIn(this)
                .addTo(workComposite)

            val updateLoginAction = intentFlow
                .filterIsInstance<AuthIntent.LoginUpdated>()
                .textDebounce()
                .map { AuthAction.UpdateLogin(it.login) }

            val updatePasswordAction = intentFlow
                .filterIsInstance<AuthIntent.PasswordUpdated>()
                .textDebounce()
                .map { AuthAction.UpdatePassword(it.password) }

            val createAuthRequest = intentFlow
                .filterIsInstance<AuthIntent.AuthClick>()
                .flatMapConcat {
                    val state = initialStateFlow.value
                    val login = state.login
                    val password = state.password
                    if (login != null && password != null) flowOf(
                        AuthRequest(login, password)
                    ) else emptyFlow()
                }

            val showLoadingAction = createAuthRequest
                .map { AuthAction.UpdateShowLoading(true) }

            val authRequestFlow = createAuthRequest
                .map { interactor.auth(it) }
                .onEach { Log.e("EEE", "$it") }

            val authRequestAction = authRequestFlow
                .flatMapConcat {
                    if (it is AuthResult.Error) flowOf(
                        AuthAction.AuthFailed,
                        AuthAction.Clear,
                    ) else emptyFlow()
                }
                .filterNotNull()

            authRequestFlow
                .filterIsInstance<AuthResult.Success>()
                .onEach {
                    val result = it.authResult
                    val user = result.user
                    val token = result.token ?: return@onEach
                    // interactor.saveUser(user, token)
                    Log.e("SAVED", "Login Succes login $user and $token")
                }
                .launchIn(this)
                .addTo(workComposite)

            createAuthRequest
                .launchIn(this)
                .addTo(workComposite)

            merge(
                authRequestAction,
                updateLoginAction,
                showLoadingAction,
                updatePasswordAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach {
                    stateFlow.emit(it)
                    initialStateFlow.value = it
                }
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: AuthState, action: AuthAction): AuthState = when (action) {
        is AuthAction.UpdateLogin -> state.copy(login = action.login)
        is AuthAction.UpdatePassword -> state.copy(password = action.password)
        is AuthAction.AuthFailed -> state.copy(isAuthFailed = true, isLoading = false)
        is AuthAction.UpdateShowLoading -> state.copy(isLoading = true)
        AuthAction.Clear -> state.copy(isAuthFailed = false, isLoading = false)
    }
}
