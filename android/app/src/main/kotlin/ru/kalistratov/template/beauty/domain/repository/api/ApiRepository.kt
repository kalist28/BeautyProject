package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*

interface ApiRepository : AuthRepository, ApiUserRepository, WorkdayWindowApiRepository {
    suspend fun loadWeekSequence(): NetworkResult<WeekSequence>
    suspend fun loadWorkdaySequence(id: Id): NetworkResult<WorkdaySequence>
    suspend fun updateWorkDaySequence(workdaySequence: WorkdaySequence): NetworkResult<WorkdaySequence>
    suspend fun createWorkDaySequence(workdaySequence: WorkdaySequence): NetworkResult<WorkdaySequence>
}
