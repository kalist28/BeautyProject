package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.repository.SequenceDayWindowsRepository
import ru.kalistratov.template.beauty.interfaces.server.service.ApiSequenceDayWindowsService
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toServer

class SequenceDayWindowsRepositoryImpl(
    private val apiSequenceDayWindowsService: ApiSequenceDayWindowsService
) : SequenceDayWindowsRepository {

    override suspend fun add(window: SequenceDayWindow): SequenceDayWindow? {
        val serverWindow = window.toServer()
        return when (serverWindow.id.isBlank()) {
            true -> apiSequenceDayWindowsService.create(serverWindow)
            else -> apiSequenceDayWindowsService.update(serverWindow)
        }.let { result ->
            when (result is NetworkResult.Success) {
                true -> result.value.toLocal()
                false -> null
            }
        }
    }

    override suspend fun get(id: Id): SequenceDayWindow? {
        return when (val result = apiSequenceDayWindowsService.get(id)) {
            is NetworkResult.Success -> result.value.toLocal()
            else -> null
        }
    }

    override suspend fun remove(id: Id) = apiSequenceDayWindowsService
        .remove(id) is NetworkResult.Success

    override suspend fun removeAll(ids: List<Id>): List<Id> {
        val unmovedIds = mutableListOf<Id>()
        ids.forEach {
            val removed = remove(it)
            if (!removed) unmovedIds.add(it)
        }
        return unmovedIds
    }
}