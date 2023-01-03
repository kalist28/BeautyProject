package ru.kalistratov.template.beauty.infrastructure.service

import android.Manifest.permission.READ_CONTACTS
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.domain.service.PermissionsService
import ru.kalistratov.template.beauty.presentation.entity.RequestPermission
import ru.kalistratov.template.beauty.presentation.entity.RequestPermissionsResult
import javax.inject.Inject

class PermissionsServiceImpl @Inject constructor() : PermissionsService {

    companion object {
        const val CONTACTS_CODE = 1
    }

    private val requests = MutableSharedFlow<RequestPermission>(
        replay = 0,
        extraBufferCapacity = 1,
        BufferOverflow.DROP_OLDEST
    )

    private val responses = MutableSharedFlow<RequestPermissionsResult>(
        replay = 0,
        extraBufferCapacity = 1,
        BufferOverflow.DROP_OLDEST
    )

    override fun requests() = requests.asSharedFlow()

    override fun responses() = responses.asSharedFlow()

    override fun pushRequestPermissionsResult(result: RequestPermissionsResult) {
        responses.tryEmit(result)
    }

    override fun requestContactsPermission() {
        requests.tryEmit(RequestPermission(CONTACTS_CODE, listOf(READ_CONTACTS)))
    }

}