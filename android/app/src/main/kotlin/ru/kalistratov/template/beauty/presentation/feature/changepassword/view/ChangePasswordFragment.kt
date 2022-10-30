package ru.kalistratov.template.beauty.presentation.feature.changepassword.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentChangePasswordBinding
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.textChanges
import ru.kalistratov.template.beauty.presentation.feature.changepassword.ChangePasswordState
import ru.kalistratov.template.beauty.presentation.feature.changepassword.ChangePasswordViewModel
import ru.kalistratov.template.beauty.presentation.feature.changepassword.di.ChangePasswordModule
import javax.inject.Inject

sealed interface ChangePasswordIntent : BaseIntent {
    data class CurrentPasswordChanged(val password: String) : ChangePasswordIntent
    data class NewPasswordChanged(val password: String) : ChangePasswordIntent
    data class ConfirmNewPasswordChanged(val password: String) : ChangePasswordIntent
}

class ChangePasswordFragment : BaseFragment(), BaseView<ChangePasswordIntent, ChangePasswordState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ChangePasswordViewModel::class.java]
    }

    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentChangePasswordBinding
        .inflate(inflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun injectUserComponent(userComponent: UserComponent) {
        userComponent.plus(ChangePasswordModule(this)).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates().collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun intents(): Flow<ChangePasswordIntent> = merge(
        binding.newPasswordEditText.textChanges()
            .map { ChangePasswordIntent.NewPasswordChanged(it.toString()) },
        binding.currentPasswordEditText.textChanges()
            .map { ChangePasswordIntent.CurrentPasswordChanged(it.toString()) },
        binding.confirmNewPasswordEditText.textChanges()
            .map { ChangePasswordIntent.ConfirmNewPasswordChanged(it.toString()) },
    )

    override fun render(state: ChangePasswordState) {
        with(binding) {
            val newPasswordErrorId = state.newPasswordErrorResId
            button.isEnabled = state.passwordsAreEquals && newPasswordErrorId == null

            newPasswordInputLayout.error = newPasswordErrorId
                ?.let { requireContext().getString(it) }

            confirmNewPasswordInputLayout.error =
                if (state.passwordsAreEquals || state.confirmNewPassword.isEmpty()) null
                else requireContext().getString(R.string.confirm_password_error)
        }
    }
}