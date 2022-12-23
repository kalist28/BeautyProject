package ru.kalistratov.template.beauty.presentation.feature.auth

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.common.checkEmailRegex
import ru.kalistratov.template.beauty.domain.entity.request.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.request.AuthResult
import ru.kalistratov.template.beauty.domain.feature.auth.AuthInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import ru.kalistratov.template.beauty.presentation.feature.auth.view.AuthIntent
import javax.inject.Inject

data class AuthState(
    val email: String? = "k@k.k",
    val password: String? = "123123",
    val isLoading: Boolean = false,
    val isAuthFailed: Boolean = false,
) : BaseState

sealed class AuthAction : BaseAction {
    object Clear : AuthAction()
    object AuthFailed : AuthAction()

    data class UpdateEmail(val email: String) : AuthAction()
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

            val updateEmailAction = intentFlow
                .filterIsInstance<AuthIntent.EmailUpdated>()
                .textDebounce()
                .flatMapConcat {
                    val email = it.email
                    val checkEmail = email matches checkEmailRegex.toRegex()
                    if (checkEmail) flowOf(AuthAction.UpdateEmail(email))
                    else emptyFlow()
                }

            val updatePasswordAction = intentFlow
                .filterIsInstance<AuthIntent.PasswordUpdated>()
                .textDebounce()
                .map { AuthAction.UpdatePassword(it.password) }

            val createAuthRequest = intentFlow
                .filterIsInstance<AuthIntent.AuthClick>()
                .flatMapConcat {
                    val state = initialStateFlow.value
                    val email = state.email
                    val password = state.password
                    if (email != null && password != null) flowOf(
                        AuthRequest(email, password)
                    ) else emptyFlow()
                }
                .share(this)

            val showLoadingAction = createAuthRequest
                .map { AuthAction.UpdateShowLoading(true) }

            val authRequestFlow = createAuthRequest
                .map { interactor.auth(it) }
                .share(this)

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
                    interactor.requestLoadUser()
                    router.openTimetable()
                }
                .launchIn(this)
                .addTo(workComposite)

            createAuthRequest
                .launchIn(this)
                .addTo(workComposite)

            merge(
                authRequestAction,
                updateEmailAction,
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
        is AuthAction.UpdateEmail -> state.copy(email = action.email)
        is AuthAction.UpdatePassword -> state.copy(password = action.password)
        is AuthAction.AuthFailed -> state.copy(isAuthFailed = true, isLoading = false)
        is AuthAction.UpdateShowLoading -> state.copy(isLoading = true)
        AuthAction.Clear -> state.copy(isAuthFailed = false, isLoading = false)
    }
}
