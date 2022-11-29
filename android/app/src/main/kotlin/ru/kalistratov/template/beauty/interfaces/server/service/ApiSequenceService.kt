package ru.kalistratov.template.beauty.interfaces.server.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerSequenceDay
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerSequenceWeek

interface ApiSequenceService {
    suspend fun getDay(dayNumber: Int): NetworkResult<ServerSequenceDay>
    suspend fun getWeek(): NetworkResult<ServerSequenceWeek>
    suspend fun createDay(day: ServerSequenceDay): NetworkResult<ServerSequenceDay>
    suspend fun updateDay(day: ServerSequenceDay): NetworkResult<ServerSequenceDay>
}
