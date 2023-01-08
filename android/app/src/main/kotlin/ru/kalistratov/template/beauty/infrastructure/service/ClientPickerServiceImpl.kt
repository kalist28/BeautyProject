package ru.kalistratov.template.beauty.infrastructure.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.service.ClientPickerService
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import javax.inject.Inject

class ClientPickerServiceImpl @Inject constructor() : ClientPickerService {

    private val selectionsMutableFLow = mutableSharedFlow<Id>()

    override suspend fun postSelected(itemId: Id) = selectionsMutableFLow.emit(itemId)

    override fun selections(): Flow<Id> = selectionsMutableFLow.asSharedFlow()
}