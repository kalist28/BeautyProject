package ru.kalistratov.template.beauty.presentation.feature.registration.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import ru.kalistratov.template.beauty.databinding.FragmentRegistrationFirstStepBinding
import ru.kalistratov.template.beauty.databinding.FragmentRegistrationSecondStepBinding
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepInfoType
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepTypedInfo

abstract class StepFragment : Fragment() {
    protected val registrationStepService by lazy {
        (activity?.applicationContext as Application)
            .applicationComponent
            .getRegistrationStepService()
    }

    protected fun setActionOnTextChanged(
        editText: EditText,
        type: StepInfoType
    ) {
        editText.doOnTextChanged { text, _, _, _ ->
            registrationStepService.sendInfo(
                StepTypedInfo(type, text.toString())
            )
        }
    }
}

class FirstStepFragment : StepFragment() {

    private lateinit var binding: FragmentRegistrationFirstStepBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegistrationFirstStepBinding
        .inflate(inflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setActionOnTextChanged(emailEditText, StepInfoType.EMAIL)
            setActionOnTextChanged(passwordEditText, StepInfoType.PASSWORD)
        }
    }

}

class SecondStepFragment : StepFragment() {

    private lateinit var binding: FragmentRegistrationSecondStepBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegistrationSecondStepBinding
        .inflate(inflater, container, false)
        .let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setActionOnTextChanged(nameEditText, StepInfoType.FIRSTNAME)
            setActionOnTextChanged(lastnameEditText, StepInfoType.LASTNAME)
            setActionOnTextChanged(patronymicEditText, StepInfoType.PATRONYMIC)
        }
    }
}
