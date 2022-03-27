package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*

interface ApiRepository : ApiUserRepository {
    suspend fun auth(request: AuthRequest): NetworkResult<ServerToken>
    suspend fun registration(request: RegistrationRequest): NetworkResult<User>

    suspend fun loadWeekSequence(): NetworkResult<WeekSequence>
    suspend fun updateWorkDaySequence(workDaySequence: WorkDaySequence): NetworkResult<WorkDaySequence>
    suspend fun createWorkDaySequence(workDaySequence: WorkDaySequence): NetworkResult<WorkDaySequence>
}
