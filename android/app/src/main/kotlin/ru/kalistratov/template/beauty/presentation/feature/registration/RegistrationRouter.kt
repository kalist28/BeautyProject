package ru.kalistratov.template.beauty.presentation.feature.registration

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.registration.view.RegistrationFragmentDirections

interface RegistrationRouter {
    fun openRegistration()
}

class RegistrationRouterImpl(private val navController: NavController) : RegistrationRouter {
    override fun openRegistration() = navController.navigate(
        RegistrationFragmentDirections.actionRegistrationFragmentToTimetableFragment()
    )
}
