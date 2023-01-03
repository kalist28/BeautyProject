package ru.kalistratov.template.beauty.presentation.feature.contactpicker

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter

interface ContactPickerRouter {
    fun back()
}

class ContactPickerRouterImpl(
    fragment: String,
    private val navController: NavController
) : BaseRouter(fragment), ContactPickerRouter {
    override fun back() {
        navController.popBackStack()
    }
}