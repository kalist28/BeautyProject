package ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows

import androidx.navigation.NavController

interface EditWorkdayWindowsRouter {
    fun back()
}

class EditWorkdayWindowsRouterImpl(
    val navController: NavController
) : EditWorkdayWindowsRouter {

    override fun back() {
        navController.popBackStack()
    }
}
