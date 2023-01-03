package ru.kalistratov.template.beauty.presentation.feature.client.list

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.client.list.view.ClientsListFragmentDirections

interface ClientsListRouter {
    fun toCreate()
    fun toEdit(id: Id)
    fun back()
}

class ClientsListRouterImpl(
    fragment: String,
    private val navController: NavController
) : BaseRouter(fragment), ClientsListRouter {
    override fun toCreate() = navController.navigate(
        ClientsListFragmentDirections.actionClientsListFragmentToEditClientFragment(null)
    )

    override fun toEdit(id: Id) = navController.navigate(
        ClientsListFragmentDirections.actionClientsListFragmentToEditClientFragment(id)
    )

    override fun back() {
        navController.popBackStack()
    }
}