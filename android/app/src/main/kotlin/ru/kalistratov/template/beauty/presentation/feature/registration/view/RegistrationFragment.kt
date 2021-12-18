package ru.kalistratov.template.beauty.presentation.feature.registration.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.AuthBaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.extension.textChanges
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationState
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationViewModel
import ru.kalistratov.template.beauty.presentation.feature.registration.di.RegistrationModule

sealed class RegistrationIntent : BaseIntent {
    object RegistrationClick : RegistrationIntent()

    data class EmailUpdated(val email: String) : RegistrationIntent()
    data class PasswordUpdated(val password: String) : RegistrationIntent()
    data class ConfirmPasswordUpdated(val password: String) : RegistrationIntent()
}

class RegistrationFragment : AuthBaseFragment(), BaseView<RegistrationIntent, RegistrationState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[RegistrationViewModel::class.java]
    }

    private lateinit var emailEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var registrationButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun injectAppComponent() =
        appComponent.plus(RegistrationModule(this)).inject(this)

    override fun findViews() {
        emailEditText = find((R.id.email_edit_text))
        passwordEditText = find(R.id.password_edit_text)
        confirmPasswordEditText = find(R.id.confirm_password_edit_text)
        emailInputLayout = find(R.id.email_input_layout)
        passwordInputLayout = find(R.id.password_input_layout)
        confirmPasswordInputLayout = find(R.id.confirm_password_input_layout)
        registrationButton = find(R.id.registration_btn)
    }

    override fun intents(): Flow<RegistrationIntent> = merge(
        emailEditText.textChanges().map { RegistrationIntent.EmailUpdated(it.toString()) },
        passwordEditText.textChanges()
            .map { RegistrationIntent.PasswordUpdated(it.toString()) },
        confirmPasswordEditText.textChanges()
            .map { RegistrationIntent.ConfirmPasswordUpdated(it.toString()) },
        registrationButton.clicks().map { RegistrationIntent.RegistrationClick },
    )

    override fun render(state: RegistrationState) {

        state.isLoading.let {
            val enable = !it
            emailInputLayout.isEnabled = enable
            passwordInputLayout.isEnabled = enable
            confirmPasswordInputLayout.isEnabled = enable
        }

        emailInputLayout.error = state.emailError
        passwordInputLayout.error = state.passwordError

        confirmPasswordInputLayout.error = if (!state.confirmPasswordValid)
            resources.getString(R.string.confirm_password_error)
        else null

        registrationButton.isEnabled = state.allowRequest
    }
}
