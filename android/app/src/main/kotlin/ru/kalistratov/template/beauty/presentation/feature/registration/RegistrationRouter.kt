package ru.kalistratov.template.beauty.presentation.feature.registration

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.registration.view.RegistrationFragmentDirections

interface RegistrationRouter {
    fun openTimetable()
}

class RegistrationRouterImpl(private val navController: NavController) : RegistrationRouter {
    override fun openTimetable() = navController.navigate(
        RegistrationFragmentDirections.actionRegistrationFragmentToTimetableFragment()
    )
}
