package ru.kalistratov.template.beauty.presentation.feature.auth

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.auth.view.AuthFragmentDirections

interface AuthRouter {
    fun openTimetable()
    fun openRegistration()
}

class AuthRouterImpl(
    private val navController: NavController,
    fragment: String
) : BaseRouter(fragment), AuthRouter {
    override fun openTimetable() = navController.safetyNavigate(
        AuthFragmentDirections.actionAuthFragmentToTimetableFragment()
    )

    override fun openRegistration() = navController.safetyNavigate(
        AuthFragmentDirections.actionAuthFragmentToRegistrationFragment()
    )
}
