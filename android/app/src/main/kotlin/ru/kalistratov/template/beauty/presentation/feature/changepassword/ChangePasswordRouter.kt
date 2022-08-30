package ru.kalistratov.template.beauty.presentation.feature.changepassword

import androidx.navigation.NavController

interface ChangePasswordRouter {
    fun exit()
}

class ChangePasswordRouterImpl(
    private val navController: NavController
): ChangePasswordRouter {
    override fun exit() { navController.popBackStack() }
}