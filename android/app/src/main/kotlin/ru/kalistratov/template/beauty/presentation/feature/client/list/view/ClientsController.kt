package ru.kalistratov.template.beauty.presentation.feature.client.list.view

import com.airbnb.epoxy.EpoxyController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.client
import ru.kalistratov.template.beauty.divider
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.indentSmall
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.ClientCardModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins

open class ClientsController : EpoxyController() {

    private val clicksMutableFlow = mutableSharedFlow<Id>()

    var clients: List<Client> = emptyList()

    override fun buildModels() = clients.map { client ->
        ClientCardModel(
            client = client,
            clickAction = { clicksMutableFlow.tryEmit(client.id) },
            marginsBundle = MarginsBundle.base.copy(bottomMarginDp = 0),
            idByClient = true
        )
    }.run(::add)


    fun clicks(): Flow<Id> = clicksMutableFlow.asSharedFlow()
}