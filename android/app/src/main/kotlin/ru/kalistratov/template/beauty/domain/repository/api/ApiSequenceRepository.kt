package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*

interface ApiSequenceRepository {
    suspend fun getDay(id: Id): NetworkResult<SequenceDay>
    suspend fun getWeek(): NetworkResult<SequenceWeek>
    suspend fun createDay(day: SequenceDay): NetworkResult<SequenceDay>
    suspend fun updateDay(day: SequenceDay): NetworkResult<SequenceDay>
}
