package ru.kalistratov.template.beauty.domain.service.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.entity.dto.ServerSequenceDayWindow

interface ApiSequenceDayWindowsService {
    suspend fun get(id: Id): NetworkResult<ServerSequenceDayWindow>
    suspend fun create(window: ServerSequenceDayWindow): NetworkResult<ServerSequenceDayWindow>
    suspend fun update(window: ServerSequenceDayWindow): NetworkResult<ServerSequenceDayWindow>
}