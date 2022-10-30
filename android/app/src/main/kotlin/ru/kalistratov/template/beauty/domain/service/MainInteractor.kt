package ru.kalistratov.template.beauty.domain.service

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.presentation.entity.RequestPermission
import ru.kalistratov.template.beauty.presentation.entity.RequestPermissionsResult

interface MainInteractor {
    suspend fun requests(): Flow<RequestPermission>
    suspend fun pushRequestPermissionsResult(result: RequestPermissionsResult)
}