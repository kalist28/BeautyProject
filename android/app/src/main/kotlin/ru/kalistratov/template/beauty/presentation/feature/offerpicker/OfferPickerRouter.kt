package ru.kalistratov.template.beauty.presentation.feature.offerpicker

import androidx.navigation.NavController

interface OfferPickerRouter {
    fun back()
}

class OfferPickerRouterImpl(
    private val navController: NavController
) : OfferPickerRouter {
    override fun back() {
        navController.popBackStack()
    }
}


