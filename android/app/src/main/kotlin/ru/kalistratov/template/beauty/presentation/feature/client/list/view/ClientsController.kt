package ru.kalistratov.template.beauty.presentation.feature.client.list.view

import com.airbnb.epoxy.EpoxyController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.client
import ru.kalistratov.template.beauty.divider
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins

class ClientsController : EpoxyController() {

    private val clicksMutableFlow = mutableSharedFlow<Id>()

    var clients: List<Client> = emptyList()

    override fun buildModels() {
        val baseMargin = MarginsBundle.base
        val clickListener: (Id) -> Unit = { clicksMutableFlow.tryEmit(it) }
        clients.forEachIndexed { index, client ->
            client {
                id(client.number)
                name("${client.name} ${client.surname ?: ""} ${client.patronymic ?: ""}")
                number(client.number)
                onBind { _, holder, _ ->
                    holder.setMargins(baseMargin)
                    holder.dataBinding.root.setOnClickListener {
                        clickListener.invoke(client.id)
                    }
                }
            }
            divider {
                id("client_$index")
                onBind { _, holder, _ -> holder.setMargins(baseMargin) }
            }
        }
    }

    fun clicks(): Flow<Id> = clicksMutableFlow.asSharedFlow()
}