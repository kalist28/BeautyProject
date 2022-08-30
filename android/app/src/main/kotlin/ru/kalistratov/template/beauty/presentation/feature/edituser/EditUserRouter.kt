package ru.kalistratov.template.beauty.presentation.feature.edituser

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.edituser.view.EditUserFragmentDirections

interface EditUserRouter {
    fun openChangePassword()
    fun exit()
}

class EditUserRouterImpl(
    private val navController: NavController
) : EditUserRouter {

    override fun openChangePassword() = navController.navigate(
        EditUserFragmentDirections.actionEditUserFragmentToChangePasswordFragment()
    )

    override fun exit() {
        navController.popBackStack()
    }
}