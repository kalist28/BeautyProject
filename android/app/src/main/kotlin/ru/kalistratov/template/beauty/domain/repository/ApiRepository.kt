package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*

interface ApiRepository {
    suspend fun registration(request: RegistrationRequest): NetworkResult<ServerAuthResult>
    suspend fun auth(request: AuthRequest): NetworkResult<ServerAuthResult>

    suspend fun loadWeekSequence(): NetworkResult<WeekSequence>
    suspend fun updateWorkDaySequence(workDaySequence: WorkDaySequence): NetworkResult<Unit>
}
