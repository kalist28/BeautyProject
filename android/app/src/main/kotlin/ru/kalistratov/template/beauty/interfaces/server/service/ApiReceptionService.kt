package ru.kalistratov.template.beauty.interfaces.server.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.interfaces.server.dto.FreeSequenceDayWindowsRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.FreeSequenceDayWindowsResponse
import ru.kalistratov.template.beauty.interfaces.server.dto.MakeReservationRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerReservation
import ru.kalistratov.template.beauty.interfaces.server.entity.IncludeType

interface ApiReceptionService {
    suspend fun loadFreeWindowsForDay(
        request: FreeSequenceDayWindowsRequest
    ): NetworkResult<FreeSequenceDayWindowsResponse>

    suspend fun makeReservation(
        request: MakeReservationRequest,
        includeType: IncludeType
    ): NetworkResult<ServerReservation>

    suspend fun loadReservations(
        date: String
    ): NetworkResult<List<ServerReservation>>
}