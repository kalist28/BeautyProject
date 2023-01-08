package ru.kalistratov.template.beauty.interfaces.server.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Data
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.interfaces.server.dto.FreeSequenceDayWindowsRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.FreeSequenceDayWindowsResponse
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerSequenceDayWindow

interface ApiReceptionService {
    suspend fun loadFreeWindowsForDay(
        request: FreeSequenceDayWindowsRequest
    ): NetworkResult<FreeSequenceDayWindowsResponse>

    suspend fun makeReservation(

    )
}