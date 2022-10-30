package ru.kalistratov.template.beauty.domain.service

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.presentation.entity.RequestPermission
import ru.kalistratov.template.beauty.presentation.entity.RequestPermissionsResult

interface PermissionsService {
    fun requests(): Flow<RequestPermission>
    fun responses(): Flow<RequestPermissionsResult>
    fun pushRequestPermissionsResult(result: RequestPermissionsResult)
    fun requestContactsPermission()
}