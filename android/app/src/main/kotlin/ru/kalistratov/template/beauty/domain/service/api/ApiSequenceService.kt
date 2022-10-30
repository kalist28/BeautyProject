package ru.kalistratov.template.beauty.domain.service.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.infrastructure.entity.dto.ServerSequenceDay
import ru.kalistratov.template.beauty.infrastructure.entity.dto.ServerSequenceWeek

interface ApiSequenceService {
    suspend fun getDay(dayNumber: Int): NetworkResult<ServerSequenceDay>
    suspend fun getWeek(): NetworkResult<ServerSequenceWeek>
    suspend fun createDay(day: ServerSequenceDay): NetworkResult<ServerSequenceDay>
    suspend fun updateDay(day: ServerSequenceDay): NetworkResult<ServerSequenceDay>
}
