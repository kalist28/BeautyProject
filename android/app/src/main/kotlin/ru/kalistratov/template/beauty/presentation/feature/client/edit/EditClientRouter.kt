package ru.kalistratov.template.beauty.presentation.feature.client.edit

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.client.edit.view.EditClientFragmentDirections

interface EditClientRouter {
    fun back()
    fun toPicker()
}

class EditClientRouterImpl(
    fragmentName: String,
    private val navController: NavController
) : BaseRouter(fragmentName), EditClientRouter {
    override fun back() {
        navController.popBackStack()
    }

    override fun toPicker() = navController.safetyNavigate(
        EditClientFragmentDirections.actionEditClientFragmentToContactsPickerFragment()
    )
}