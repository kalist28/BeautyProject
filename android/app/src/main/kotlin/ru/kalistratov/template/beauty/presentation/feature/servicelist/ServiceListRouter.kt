package ru.kalistratov.template.beauty.presentation.feature.servicelist

import androidx.navigation.NavController

interface ServiceListRouter {
    fun back()
}

class ServiceListRouterImpl(
    private val navController: NavController
) : ServiceListRouter {
    override fun back() {
        navController.popBackStack()
    }
}


