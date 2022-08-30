package ru.kalistratov.template.beauty.presentation.feature.registration

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.common.checkEmailRegex
import ru.kalistratov.template.beauty.domain.entity.request.AuthResult
import ru.kalistratov.template.beauty.domain.entity.request.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.feature.registration.RegistrationInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepInfoType
import ru.kalistratov.template.beauty.presentation.feature.registration.view.FirstStepFragmentDirections
import ru.kalistratov.template.beauty.presentation.feature.registration.view.RegistrationFragment.Companion.STEP_COUNT
import ru.kalistratov.template.beauty.presentation.feature.registration.view.RegistrationIntent
import javax.inject.Inject

data class RegistrationState(
    val email: String = "",
    val password: String = "",
    val firstname: String = "",
    val lastName: String = "",
    val patronymic: String = "",
    val emailValid: Boolean = true,
    val passwordValid: Boolean = false,
    val allowRequest: Boolean = false,
    val isLoading: Boolean = false,
    val user: User? = null,
    val registrationSuccess: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val step: Int = 1,
) : BaseState

sealed class RegistrationAction : BaseAction {
    data class UpdateStep(val step: Int) : RegistrationAction()
    data class UpdateAllowRequest(val isAllow: Boolean) : RegistrationAction()
    data class UpdateEmail(val email: String, val matches: Boolean) : RegistrationAction()
    data class UpdateFirstName(val name: String) : RegistrationAction()
    data class UpdateLastname(val lastName: String) : RegistrationAction()
    data class UpdatePatronymic(val patronymic: String) : RegistrationAction()
    data class UpdatePassword(val password: String) : RegistrationAction()
    data class UpdateShowLoading(val show: Boolean) : RegistrationAction()
    data class UpdatePasswordValid(val isValid: Boolean) : RegistrationAction()
    data class RegistrationSuccess(val user: User) : RegistrationAction()
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

    internal var stepsController: NavController? = null

    private val initialState = RegistrationState()

    private val initialStateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val stepInfoUpdatesFlow = interactor.stepInfoChanges()
                .share(this)

            val updateEmailAction = stepInfoUpdatesFlow
                .filter { it.type == StepInfoType.EMAIL }
                .textDebounce()
                .map {
                    val email = it.value
                    val matches = email matches checkEmailRegex.toRegex()
                    RegistrationAction.UpdateEmail(email, matches)
                }

            val updatePasswordAction = stepInfoUpdatesFlow
                .filter { it.type == StepInfoType.PASSWORD }
                .textDebounce()
                .flatMapConcat {
                    flowOf(
                        RegistrationAction.UpdatePassword(it.value),
                        RegistrationAction.UpdatePasswordValid(it.value.length >= 8),
                    )
                }

            val fullNameUpdatesAction = stepInfoUpdatesFlow
                .textDebounce()
                .flatMapConcat {
                    when(it.type) {
                        StepInfoType.LASTNAME -> flowOf(RegistrationAction.UpdateFirstName(it.value))
                        StepInfoType.FIRSTNAME -> flowOf(RegistrationAction.UpdateLastname(it.value))
                        StepInfoType.PATRONYMIC -> flowOf(RegistrationAction.UpdatePatronymic(it.value))
                        else -> emptyFlow()
                    }
                }

            val updateAllowRequestAction = stateUpdates().flatMapConcat { state ->
                val emailCheck = state.emailValid && state.email.isNotBlank()
                val password = state.password.isNotBlank()
                val passwordIsValid = state.passwordValid
                val oldAllowRequest = state.allowRequest
                val fioLength = 2
                val fioValid = state.firstname.length >= fioLength &&
                        state.lastName.length >= fioLength &&
                        state.patronymic.length >= fioLength

                val result = (emailCheck && password && passwordIsValid)
                    .let {
                        if (state.step != STEP_COUNT) it
                        else it && fioValid
                    }

                if (result == oldAllowRequest) emptyFlow()
                else flowOf(RegistrationAction.UpdateAllowRequest(result))
            }

            val nextStepClicksFlow = intentFlow
                .filterIsInstance<RegistrationIntent.NextStepClick>()
                .share(this)

            val nextStepFlow = nextStepClicksFlow
                .map {
                    val step = initialStateFlow.value.step
                    val direction = when (step) {
                        1 -> FirstStepFragmentDirections
                            .actionFirstStepFragmentToSecondStepFragment()
                        else -> null
                    }
                    direction?.let { stepsController?.navigate(it) }
                    if (direction == null) step
                    else step + 1
                }
                .flowOn(Dispatchers.Main)

            val backStepFlow = intentFlow
                .filterIsInstance<RegistrationIntent.BackPress>()
                .map { initialStateFlow.value.step - 1 }

            val updateStepAction = merge(
                nextStepFlow, backStepFlow
            ).map { RegistrationAction.UpdateStep(it) }

            val createRegistrationRequestSharedFlow = nextStepClicksFlow
                .flatMapConcat {
                    val state = initialStateFlow.value
                    if (state.step != STEP_COUNT) return@flatMapConcat emptyFlow()
                    val registrationStepIsLast = state.step == STEP_COUNT

                    if (registrationStepIsLast) flowOf(
                        state.run {
                            RegistrationRequest(
                                email,
                                password,
                                firstname,
                                lastName,
                                patronymic
                            )
                        }
                    ) else emptyFlow()
                }
                .share(this)

            val showLoadingAction = createRegistrationRequestSharedFlow
                .map { RegistrationAction.UpdateShowLoading(true) }

            val registrationRequestFlow = createRegistrationRequestSharedFlow
                .map { interactor.registration(it) }
                .share(this)

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
                    interactor.requestLoadUser()
                    router.openTimetable()
                }
                .launchIn(this)
                .addTo(workComposite)

            merge(
                updateStepAction,
                showLoadingAction,
                updateEmailAction,
                updatePasswordAction,
                fullNameUpdatesAction,
                updateAllowRequestAction,
                registrationRequestAction,
            )
                .flowOn(Dispatchers.IO)
                .scan(RegistrationState(), ::reduce)
                .onEach { initialStateFlow.value = it }
                .collect(stateFlow)

            createRegistrationRequestSharedFlow
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: RegistrationState, action: RegistrationAction) = when (action) {
        is RegistrationAction.RegistrationSuccess -> state.copy(
            registrationSuccess = true,
            user = action.user,
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
        is RegistrationAction.UpdatePasswordValid -> state.copy(
            passwordValid = action.isValid
        )
        is RegistrationAction.UpdateAllowRequest -> state.copy(allowRequest = action.isAllow)
        is RegistrationAction.UpdateStep -> state.copy(step = action.step)
        is RegistrationAction.UpdateFirstName -> state.copy(firstname = action.name)
        is RegistrationAction.UpdateLastname -> state.copy(lastName = action.lastName)
        is RegistrationAction.UpdatePatronymic -> state.copy(patronymic = action.patronymic)
    }
}
