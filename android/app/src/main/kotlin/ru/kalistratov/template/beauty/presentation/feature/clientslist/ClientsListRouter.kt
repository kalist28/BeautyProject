package ru.kalistratov.template.beauty.presentation.feature.clientslist

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter

interface ClientsListRouter {
}

class ClientsListRouterImpl(
    fragment: String,
    private val navController: NavController
) : BaseRouter(fragment), ClientsListRouter{
}