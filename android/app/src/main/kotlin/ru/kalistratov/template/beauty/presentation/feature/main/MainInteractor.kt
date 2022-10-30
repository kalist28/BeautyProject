package ru.kalistratov.template.beauty.presentation.feature.main

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.domain.service.MainInteractor
import ru.kalistratov.template.beauty.domain.service.PermissionsService
import ru.kalistratov.template.beauty.presentation.entity.RequestPermission
import ru.kalistratov.template.beauty.presentation.entity.RequestPermissionsResult

class MainInteractorImpl(
    private val permissionsService: PermissionsService
) : MainInteractor {
    override suspend fun requests(): Flow<RequestPermission> = permissionsService.requests()

    override suspend fun pushRequestPermissionsResult(result: RequestPermissionsResult) =
        permissionsService.pushRequestPermissionsResult(result)
}