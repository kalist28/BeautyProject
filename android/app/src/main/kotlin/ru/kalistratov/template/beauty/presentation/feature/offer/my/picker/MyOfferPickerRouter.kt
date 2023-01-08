package ru.kalistratov.template.beauty.presentation.feature.offer.my.picker

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.SafetyRouter

interface MyOfferPickerRouter {
    fun back()
}

class MyOfferPickerRouterImpl(
    override val fragmentName: String,
    private val navController: NavController
) : SafetyRouter(), MyOfferPickerRouter {
    override fun back() {
        navController.popBackStack()
    }
}