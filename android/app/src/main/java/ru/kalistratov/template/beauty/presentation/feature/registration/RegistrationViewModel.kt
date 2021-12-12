package ru.kalistratov.template.beauty.presentation.feature.registration

import android.util.Log
import androidx.lifecycle.viewModelScope
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import ru.kalistratov.template.beauty.presentation.feature.registration.view.RegistrationIntent

data class RegistrationState(
    val login: String? = null,
    val password: String? = null,
    val isLoading: Boolean = false,
    val serverUser: ServerUser? = null,
    val isRegistrationSuccess: Boolean = false,
    val errorMessage: String? = null,
    val loginError: String? = null,
    val passwordError: String? = null,
) : BaseState

sealed class RegistrationAction : BaseAction {
    data class UpdateLogin(val login: String) : RegistrationAction()
    data class UpdatePassword(val password: String) : RegistrationAction()
    data class UpdateShowLoading(val show: Boolean) : RegistrationAction()
    data class RegistrationSuccess(val serverUser: ServerUser) : RegistrationAction()
    data class RegistrationError(val message: String?) : RegistrationAction()
    data class UpdateRegistrationErrors(
        val message: String?,
        val loginError: String?,
        val passwordError: String?,
    ) : RegistrationAction()
}

class RegistrationViewModel @Inject constructor(
    private val interactor: RegistrationInteractor,
) : BaseViewModel<RegistrationIntent, RegistrationAction, RegistrationState>() {

    private val initialState = RegistrationState()

    private val initialStateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val updateLoginAction = intentFlow
                .filterIsInstance<RegistrationIntent.LoginUpdated>()
                .textDebounce()
                .map { RegistrationAction.UpdateLogin(it.login) }

            val updatePasswordAction = intentFlow
                .filterIsInstance<RegistrationIntent.PasswordUpdated>()
                .textDebounce()
                .map { RegistrationAction.UpdatePassword(it.password) }

            val createRegistrationRequest = intentFlow
                .filterIsInstance<RegistrationIntent.RegistrationClick>()
                .flatMapConcat {
                    val state = initialStateFlow.value
                    val login = state.login
                    val password = state.password
                    val random = Random()
                    if (login != null && password != null) flowOf(
                        RegistrationRequest(
                            login,
                            "${random.nextInt()}@mail.ru",
                            password,
                            password,
                        )
                    ) else emptyFlow()
                }

            val showLoadingAction = createRegistrationRequest
                .map { RegistrationAction.UpdateShowLoading(true) }

            val registrationRequestFlow = createRegistrationRequest
                .map { interactor.registration(it) }

            val registrationRequestAction = registrationRequestFlow
                .map {
                    if (it is AuthResult.Error) {
                        val registrationError = it.authError
                        val errors = registrationError?.errors
                        val errorMessage = registrationError?.message

                        Log.e("VM", "Registration error =  $registrationError")
                        if (errors == null) RegistrationAction.RegistrationError(errorMessage)
                        else RegistrationAction.UpdateRegistrationErrors(
                            errorMessage,
                            errors.login.firstOrNull(),
                            errors.password.firstOrNull(),
                        )
                    } else null
                }
                .filterNotNull()

            registrationRequestFlow
                .filterIsInstance<AuthResult.Success>()
                .onEach {
                    val result = it.authResult
                    val user = result.user
                    val token = result.token ?: return@onEach
                    //interactor.saveUser(user, token)
                    Log.e("SAVED", "Login Succes login $user and $token")
                }
                .launchIn(this)
                .addTo(workComposite)

            createRegistrationRequest
                .launchIn(this)
                .addTo(workComposite)

            merge(
                showLoadingAction,
                updateLoginAction,
                updatePasswordAction,
                registrationRequestAction,
            )
                .flowOn(Dispatchers.IO)
                .scan(RegistrationState(), ::reduce)
                .onEach {
                    stateFlow.emit(it)
                    initialStateFlow.value = it
                }
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: RegistrationState, action: RegistrationAction) = when (action) {
        is RegistrationAction.RegistrationSuccess -> state.copy(
            isRegistrationSuccess = true,
            serverUser = action.serverUser,
        )
        is RegistrationAction.UpdateShowLoading -> state.copy(isLoading = action.show)
        is RegistrationAction.UpdateLogin -> state.copy(login = action.login, loginError = null)
        is RegistrationAction.UpdatePassword -> state.copy(
            password = action.password,
            passwordError = null
        )
        is RegistrationAction.UpdateRegistrationErrors -> state.copy(
            errorMessage = action.message,
            loginError = action.loginError,
            passwordError = action.passwordError,
            isLoading = false,
        )
        is RegistrationAction.RegistrationError -> state.copy(
            passwordError = action.message,
            isLoading = false,
        )
    }
}
