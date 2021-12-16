package ru.kalistratov.template.beauty.presentation.feature.auth.view

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.AuthBaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.*
import ru.kalistratov.template.beauty.presentation.feature.auth.AuthState
import ru.kalistratov.template.beauty.presentation.feature.auth.AuthViewModel
import ru.kalistratov.template.beauty.presentation.feature.auth.di.AuthModule

sealed class AuthIntent : BaseIntent {
    object AuthClick : AuthIntent()
    object RegistrationClick : AuthIntent()

    data class EmailUpdated(val email: String) : AuthIntent()
    data class PasswordUpdated(val password: String) : AuthIntent()
}

class AuthFragment : AuthBaseFragment(), BaseView<AuthIntent, AuthState> {

    companion object {
        fun instance() = instanceOf<AuthFragment>()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
    }

    private lateinit var emailEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var authButton: Button
    private lateinit var registrationButton: Button

    override fun findViews() {
        emailEditText = find((R.id.email_edit_text))
        passwordEditText = find(R.id.password_edit_text)
        emailInputLayout = find(R.id.email_input_layout)
        passwordInputLayout = find(R.id.password_input_layout)
        authButton = find(R.id.auth_btn)
        registrationButton = find(R.id.registration_btn)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_auth, container, false)

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
        appComponent.plus(AuthModule(this)).inject(this)

    override fun intents(): Flow<AuthIntent> = merge(
        authButton.clicks().map { AuthIntent.AuthClick },
        registrationButton.clicks().map { AuthIntent.RegistrationClick },
        emailEditText.textChanges().map { AuthIntent.EmailUpdated(it.toString()) },
        passwordEditText.textChanges().map { AuthIntent.PasswordUpdated(it.toString()) },
    )

    override fun render(state: AuthState) {
        if (state.isAuthFailed) toast("Неверный логин или пароль.")

        state.isLoading.let {
            val enable = !it
            emailInputLayout.isEnabled = enable
            passwordInputLayout.isEnabled = enable
        }
    }
}
