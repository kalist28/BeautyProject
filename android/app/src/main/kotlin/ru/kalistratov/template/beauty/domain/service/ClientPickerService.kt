package ru.kalistratov.template.beauty.domain.service

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.domain.entity.Id

interface ClientPickerService {
    suspend fun postSelected(itemId: Id)
    fun selections(): Flow<Id>
}