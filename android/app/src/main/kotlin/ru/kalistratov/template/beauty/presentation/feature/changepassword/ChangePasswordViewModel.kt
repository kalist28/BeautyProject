package ru.kalistratov.template.beauty.presentation.feature.changepassword

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.feature.changepassword.ChangePasswordInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import ru.kalistratov.template.beauty.presentation.feature.changepassword.view.ChangePasswordIntent
import javax.inject.Inject

data class ChangePasswordState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val passwordsAreEquals: Boolean = false,
    @StringRes val newPasswordErrorResId: Int? = null,
) : BaseState

sealed interface ChangePasswordAction : BaseAction {
    data class UpdateCurrentPassword(val password: String) : ChangePasswordAction

    data class UpdateNewPasswordError(@StringRes val error: Int?) : ChangePasswordAction

    data class UpdateNewPassword(
        val password: String,
        val passwordsAreEquals: Boolean
    ) : ChangePasswordAction

    data class UpdateConfirmNewPassword(
        val password: String,
        val passwordsAreEquals: Boolean
    ) : ChangePasswordAction
}

class ChangePasswordViewModel @Inject constructor(
    private val interactor: ChangePasswordInteractor,
    private val router: ChangePasswordRouter
) : BaseViewModel<ChangePasswordIntent, ChangePasswordAction, ChangePasswordState>() {

    companion object {
        private const val PASSWORD_LENGTH = 8
    }

    private val initialState = ChangePasswordState()
    private val actualStateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val updatePasswordAction = intentFlow
                .filterIsInstance<ChangePasswordIntent.CurrentPasswordChanged>()
                .textDebounce()
                .map { ChangePasswordAction.UpdateCurrentPassword(it.password) }

            val newPasswordUpdatesSharedFlow = intentFlow
                .filterIsInstance<ChangePasswordIntent.NewPasswordChanged>()
                .textDebounce()
                .share(this)

            val updateNewPasswordErrorAction = newPasswordUpdatesSharedFlow
                .map {
                    val error = if (it.password.length >= PASSWORD_LENGTH) null
                    else R.string.short_password_error
                    ChangePasswordAction.UpdateNewPasswordError(error)
                }

            val updateNewPasswordAction = newPasswordUpdatesSharedFlow
                .map {
                    val newPassword = it.password
                    val confirmPassword = actualStateFlow.value.confirmNewPassword
                    ChangePasswordAction.UpdateNewPassword(
                        newPassword,
                        newPassword == confirmPassword
                    )
                }

            val updateConfirmNewPasswordAction = intentFlow
                .filterIsInstance<ChangePasswordIntent.ConfirmNewPasswordChanged>()
                .textDebounce()
                .map {
                    val newPassword = actualStateFlow.value.newPassword
                    val confirmPassword = it.password
                    ChangePasswordAction.UpdateConfirmNewPassword(
                        confirmPassword,
                        newPassword == confirmPassword
                    )
                }

            merge(
                updatePasswordAction,
                updateNewPasswordAction,
                updateNewPasswordErrorAction,
                updateConfirmNewPasswordAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach { actualStateFlow.value = it }
                .collect(stateFlow)

        }.addTo(workComposite)
    }

    override fun reduce(
        state: ChangePasswordState,
        action: ChangePasswordAction
    ): ChangePasswordState = when (action) {
        is ChangePasswordAction.UpdateConfirmNewPassword -> state.copy(
            confirmNewPassword = action.password,
            passwordsAreEquals = action.passwordsAreEquals
        )
        is ChangePasswordAction.UpdateCurrentPassword -> state.copy(
            currentPassword = action.password
        )
        is ChangePasswordAction.UpdateNewPassword -> state.copy(
            newPassword = action.password,
            passwordsAreEquals = action.passwordsAreEquals
        )
        is ChangePasswordAction.UpdateNewPasswordError -> state.copy(
            newPasswordErrorResId = action.error
        )
    }
}