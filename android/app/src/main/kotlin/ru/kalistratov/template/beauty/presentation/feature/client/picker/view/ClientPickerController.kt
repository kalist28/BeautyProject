package ru.kalistratov.template.beauty.presentation.feature.client.picker.view

import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.feature.client.list.view.ClientsController

class ClientPickerController : ClientsController() {

    private val buildFinishedUpdates = mutableSharedFlow<Unit>()

    override fun buildModels() {
        super.buildModels()
        buildFinishedUpdates.tryEmit(Unit)
    }

    fun buildFinishedUpdates() = buildFinishedUpdates.asSharedFlow()
}