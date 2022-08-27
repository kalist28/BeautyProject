package ru.kalistratov.template.beauty.presentation.feature.registration.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentRegistrationBinding
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.AuthBaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.feature.main.view.AdditionalBackPressCallBackOwner
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationState
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationViewModel
import ru.kalistratov.template.beauty.presentation.feature.registration.di.RegistrationModule
import javax.inject.Inject

sealed class RegistrationIntent : BaseIntent {
    object BackPress : RegistrationIntent()
    object NextStepClick : RegistrationIntent()
}

class RegistrationFragment : AuthBaseFragment(),
    BaseView<RegistrationIntent, RegistrationState> {

    companion object {
        const val STEP_COUNT = 2
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[RegistrationViewModel::class.java]
    }

    private var additionalCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            backPressedClicks.tryEmit(Unit)
            if (!navStepsController.popBackStack())
                this.isEnabled = false
        }
    }

    private val navStepsController by lazy {
        childFragmentManager
            .let { it.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
            .navController
    }

    private lateinit var binding: FragmentRegistrationBinding
    private val backPressedClicks = MutableSharedFlow<Unit>(extraBufferCapacity = 2)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity()
            .let { it as? AdditionalBackPressCallBackOwner }
            ?.addAdditionalCallback(additionalCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegistrationBinding
        .inflate(inflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavGrath()

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
            this.stepsController = navStepsController
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity()
            .let { it as? AdditionalBackPressCallBackOwner }
            ?.removeAdditionalCallback()
    }

    private fun setNavGrath() = navStepsController.also {
        val navGraph = it.navInflater.inflate(R.navigation.reg_steps_graph)
        it.setGraph(navGraph, null)
    }

    override fun injectAppComponent() =
        appComponent.plus(RegistrationModule(this)).inject(this)

    override fun intents(): Flow<RegistrationIntent> = merge(
        backPressedClicks.map { RegistrationIntent.BackPress },
        binding.nextStepButton.clicks().map { RegistrationIntent.NextStepClick }
    )

    override fun render(state: RegistrationState) {
        additionalCallback.isEnabled = state.step > 1
        binding.nextStepButton.apply {
            text = requireContext().getString(
                if (state.step == STEP_COUNT) R.string.registration
                else R.string.next
            )
            isEnabled = state.allowRequest
        }
        binding.progressBar.isVisible = state.isLoading
    }
}
