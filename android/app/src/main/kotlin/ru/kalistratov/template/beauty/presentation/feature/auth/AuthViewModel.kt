package ru.kalistratov.template.beauty.presentation.feature.auth

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.common.checkEmailRegex
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.feature.auth.AuthInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import javax.inject.Inject

data class AuthState(
    val email: String? = null,
    val emailValid: Boolean = true,
    val password: String? = null,
    val isLoading: Boolean = false,
    val allowRequest: Boolean = false,
    val user: User? = null,
    val confirmPasswordValid: Boolean = true,
    val authSuccess: Boolean = false,
    val isAuthFailed: Boolean = false,
    val errorMessage: String? = null,
    val passwordError: String? = null,
    val emailError: String? = null,
) : BaseState

sealed class AuthAction : BaseAction {
    object Clear : AuthAction()
    object AuthFailed : AuthAction()
    data class UpdateEmail(val email: String, val matches: Boolean) : AuthAction()
    data class UpdateAllowRequest(val isAllow: Boolean) : AuthAction()
    data class UpdatePassword(val password: String) : AuthAction()
    data class UpdatePasswordValid(val isValid: Boolean) : AuthAction()
    data class UpdateShowLoading(val show: Boolean) : AuthAction()
    data class AuthSuccess(val user: User) : AuthAction()
    data class AuthError(val message: String?) : AuthAction()
    //data class RegistrationMessage(val message: String) : AuthAction()
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
                .map {
                    val email = it.email
                    val matches = email matches checkEmailRegex.toRegex()
                    AuthAction.UpdateEmail(email, matches)
                }

            val updatePasswordAction = intentFlow
                .filterIsInstance<AuthIntent.PasswordUpdated>()
                .textDebounce()
                .map { AuthAction.UpdatePassword(it.password) }

            val updateAllowRequestAction = stateUpdates().flatMapConcat { state ->
                val emailCheck = state.emailValid && !state.email.isNullOrBlank()
                val password = state.password?.isNotBlank() ?: false
                val confirmPasswordValid = state.confirmPasswordValid
                val oldAllowRequest = state.allowRequest

                val result = emailCheck && password && confirmPasswordValid
                if (result == oldAllowRequest) emptyFlow()
                else flowOf(AuthAction.UpdateAllowRequest(result))
            }

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

            val showLoadingAction = createAuthRequest
                .map { AuthAction.UpdateShowLoading(true) }

            val authRequestFlow = createAuthRequest
                .map { interactor.auth(it) }
                .onEach { Log.e("EEE", "$it") }

            val authRequestAction = authRequestFlow
                .filterIsInstance<AuthResult.Error>()
                .map {
                    val registrationError = it.authError
                    val errors = registrationError?.errors
                    val errorMessage = registrationError?.message
                    if (errors == null) AuthAction.AuthError(errorMessage)
                    else emptyFlow()
                }
            authRequestFlow
                .filterIsInstance<AuthResult.Success>()
                .onEach {
                    val result = it.authResult
                    val user = result.user.email
                        ?: throw IllegalStateException("In AuthResult user is null")
                    val token = result.token ?: return@onEach
                    interactor.saveUser(user, token)
                    router.openRegistration()
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
                updateEmailAction,
                updatePasswordAction,
                updateAllowRequestAction,
                authRequestAction,
                updatePasswordValidAction,
            )
                .flowOn(Dispatchers.IO)
                .scan(AuthState(), ::reduce)
                .onEach {
                    shareStateFlow.emit(it)
                    initialStateFlow.value = it
                }
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: AuthState, action: AuthAction): AuthState = when (action) {
        is AuthAction.AuthSuccess -> state.copy(
            authSuccess = true,
            user = action.user,
        )
        is AuthAction.UpdateShowLoading -> state.copy(isLoading = action.show)
        is AuthAction.UpdateEmail -> state.copy(
            emailValid = action.matches,
            email = action.email,
            emailError = null,
        )
        is AuthAction.UpdatePassword -> state.copy(
            password = action.password,
            passwordError = null,
        )
        is AuthAction.AuthError -> state.copy(
            errorMessage = action.message,
            isLoading = false,
        )
        is AuthAction.UpdatePasswordValid -> state.copy(
            confirmPasswordValid = action.isValid
        )
        is AuthAction.UpdateAllowRequest -> state.copy(
            allowRequest = action.isAllow
        )
        is AuthAction.AuthFailed -> state.copy(
            isAuthFailed = true,
            isLoading = false
        )
        is AuthAction.Clear -> state.copy(
            isAuthFailed = false,
            isLoading = false
        )
    }
}
