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

    data class LoginUpdated(val login: String) : RegistrationIntent()
    data class PasswordUpdated(val password: String) : RegistrationIntent()
}

class RegistrationFragment : AuthBaseFragment(), BaseView<RegistrationIntent, RegistrationState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[RegistrationViewModel::class.java]
    }

    private lateinit var loginEditText: TextInputEditText
    private lateinit var loginInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
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
        loginEditText = find((R.id.login_edit_text))
        passwordEditText = find(R.id.password_edit_text)
        registrationButton = find(R.id.registration_btn)
        loginInputLayout = find(R.id.login_input_layout)
        passwordInputLayout = find(R.id.password_input_layout)
    }

    override fun render(state: RegistrationState) {
        loginInputLayout.error = state.loginError
        passwordInputLayout.error = state.passwordError

        state.isLoading.let {
            val enable = !it
            loginInputLayout.isEnabled = enable
            passwordInputLayout.isEnabled = enable
        }
    }

    override fun intents(): Flow<RegistrationIntent> = merge(
        loginEditText.textChanges().map { RegistrationIntent.LoginUpdated(it.toString()) },
        passwordEditText.textChanges()
            .map { RegistrationIntent.PasswordUpdated(it.toString()) },
        registrationButton.clicks().map { RegistrationIntent.RegistrationClick },
    )
}
