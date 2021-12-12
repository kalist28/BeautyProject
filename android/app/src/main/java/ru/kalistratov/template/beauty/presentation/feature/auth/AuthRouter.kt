package ru.kalistratov.template.beauty.presentation.feature.auth

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.auth.view.AuthFragmentDirections

interface AuthRouter {
    fun openRegistration()
}

class AuthRouterImpl(private val navController: NavController) : AuthRouter {
    override fun openRegistration() = navController.navigate(
        AuthFragmentDirections.actionAuthFragmentToRegistrationFragment()
    )
}
