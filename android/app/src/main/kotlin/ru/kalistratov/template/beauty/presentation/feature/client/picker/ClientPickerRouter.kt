package ru.kalistratov.template.beauty.presentation.feature.client.picker

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.SafetyRouter

interface ClientPickerRouter {
    fun back()
}

class ClientPickerRouterImpl(
    override val fragmentName: String,
    private val navController: NavController
): SafetyRouter(), ClientPickerRouter {
    override fun back() {
        navController.popBackStack()
    }
}