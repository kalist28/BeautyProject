package ru.kalistratov.template.beauty.presentation.feature.registration

import androidx.lifecycle.viewModelScope
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.common.checkEmailRegex
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.feature.registration.RegistrationInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import ru.kalistratov.template.beauty.presentation.feature.registration.view.RegistrationIntent

data class RegistrationState(
    val email: String? = null,
    val emailValid: Boolean = true,
    val password: String? = null,
    val confirmPassword: String? = null,
    val confirmPasswordValid: Boolean = true,
    val allowRequest: Boolean = false,
    val isLoading: Boolean = false,
    val serverUser: ServerUser? = null,
    val registrationSuccess: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
) : BaseState

sealed class RegistrationAction : BaseAction {
    data class UpdateAllowRequest(val isAllow: Boolean) : RegistrationAction()
    data class UpdateEmail(val email: String, val matches: Boolean) : RegistrationAction()
    data class UpdatePassword(val password: String) : RegistrationAction()
    data class UpdateConfirmPassword(val password: String) : RegistrationAction()
    data class UpdateShowLoading(val show: Boolean) : RegistrationAction()
    data class UpdateConfirmPasswordValid(val isValid: Boolean) : RegistrationAction()
    data class RegistrationSuccess(val serverUser: ServerUser) : RegistrationAction()
    data class RegistrationError(val message: String?) : RegistrationAction()
    data class UpdateRegistrationErrors(
        val message: String?,
        val emailError: String?,
        val passwordError: String?,
    ) : RegistrationAction()
}

class RegistrationViewModel @Inject constructor(
    private val interactor: RegistrationInteractor,
    private val router: RegistrationRouter,
) : BaseViewModel<RegistrationIntent, RegistrationAction, RegistrationState>() {

    private val initialState = RegistrationState()

    private val initialStateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val updateEmailAction = intentFlow
                .filterIsInstance<RegistrationIntent.EmailUpdated>()
                .textDebounce()
                .map {
                    val email = it.email
                    val matches = email matches checkEmailRegex.toRegex()
                    RegistrationAction.UpdateEmail(email, matches)
                }

            val updatePasswordAction = intentFlow
                .filterIsInstance<RegistrationIntent.PasswordUpdated>()
                .textDebounce()
                .map { RegistrationAction.UpdatePassword(it.password) }

            val updateConfirmPasswordAction = intentFlow
                .filterIsInstance<RegistrationIntent.ConfirmPasswordUpdated>()
                .textDebounce()
                .map { RegistrationAction.UpdateConfirmPassword(it.password) }

            val updateConfirmPasswordValidAction = merge(
                updateConfirmPasswordAction,
                updatePasswordAction
            )
                .map {
                    val state = initialStateFlow.value
                    val password = when (it) {
                        is RegistrationAction.UpdatePassword -> it.password
                        else -> state.password
                    }
                    val confirmPassword = when (it) {
                        is RegistrationAction.UpdateConfirmPassword -> it.password
                        else -> state.confirmPassword
                    }
                    RegistrationAction.UpdateConfirmPasswordValid(password == confirmPassword)
                }

            val updateAllowRequestAction = stateUpdates().flatMapConcat { state ->
                val emailCheck = state.emailValid && !state.email.isNullOrBlank()
                val password = state.password?.isNotBlank() ?: false
                val confirmPasswordIsValid = state.confirmPasswordValid
                val oldAllowRequest = state.allowRequest

                val result = emailCheck && password && confirmPasswordIsValid
                if (result == oldAllowRequest) emptyFlow()
                else flowOf(RegistrationAction.UpdateAllowRequest(result))
            }

            val createRegistrationRequestSharedFlow = intentFlow
                .filterIsInstance<RegistrationIntent.RegistrationClick>()
                .flatMapConcat {
                    val state = initialStateFlow.value
                    val email = state.email
                    val password = state.password
                    val confirmPassword = state.confirmPassword
                    if (email != null && password != null && confirmPassword != null) flowOf(
                        RegistrationRequest(
                            email,
                            password,
                            confirmPassword,
                        )
                    ) else emptyFlow()
                }
                .share(this)

            val showLoadingAction = createRegistrationRequestSharedFlow
                .map { RegistrationAction.UpdateShowLoading(true) }

            val registrationRequestFlow = createRegistrationRequestSharedFlow
                .map { interactor.registration(it) }

            val registrationRequestAction = registrationRequestFlow
                .filterIsInstance<AuthResult.Error>()
                .map {
                    val registrationError = it.authError
                    val errors = registrationError?.errors
                    val errorMessage = registrationError?.message

                    if (errors == null) RegistrationAction.RegistrationError(errorMessage)
                    else RegistrationAction.UpdateRegistrationErrors(
                        errorMessage,
                        errors.email.firstOrNull(),
                        errors.password.firstOrNull(),
                    )
                }

            registrationRequestFlow
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

            merge(
                showLoadingAction,
                updateEmailAction,
                updatePasswordAction,
                updateAllowRequestAction,
                updateConfirmPasswordAction,
                registrationRequestAction,
                updateConfirmPasswordValidAction,
            )
                .flowOn(Dispatchers.IO)
                .scan(RegistrationState(), ::reduce)
                .onEach {
                    stateFlow.emit(it)
                    initialStateFlow.value = it
                }
                .launchIn(this)
                .addTo(workComposite)

            createRegistrationRequestSharedFlow
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: RegistrationState, action: RegistrationAction) = when (action) {
        is RegistrationAction.RegistrationSuccess -> state.copy(
            registrationSuccess = true,
            serverUser = action.serverUser,
        )
        is RegistrationAction.UpdateShowLoading -> state.copy(isLoading = action.show)
        is RegistrationAction.UpdateEmail -> state.copy(
            emailValid = action.matches,
            email = action.email,
            emailError = null
        )
        is RegistrationAction.UpdatePassword -> state.copy(
            password = action.password,
            passwordError = null
        )
        is RegistrationAction.UpdateConfirmPassword -> state.copy(confirmPassword = action.password)
        is RegistrationAction.UpdateRegistrationErrors -> state.copy(
            errorMessage = action.message,
            emailError = action.emailError,
            passwordError = action.passwordError,
            isLoading = false,
        )
        is RegistrationAction.RegistrationError -> state.copy(
            errorMessage = action.message,
            isLoading = false,
        )
        is RegistrationAction.UpdateConfirmPasswordValid -> state.copy(
            confirmPasswordValid = action.isValid
        )
        is RegistrationAction.UpdateAllowRequest -> state.copy(allowRequest = action.isAllow)
    }
}
