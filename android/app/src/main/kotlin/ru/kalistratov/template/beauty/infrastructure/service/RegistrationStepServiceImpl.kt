package ru.kalistratov.template.beauty.infrastructure.service

import kotlinx.coroutines.flow.MutableSharedFlow
import ru.kalistratov.template.beauty.domain.service.RegistrationStepService
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepTypedInfo

class RegistrationStepServiceImpl : RegistrationStepService {

    private val infoUpdates = MutableSharedFlow<StepTypedInfo>(extraBufferCapacity = 4)
    private val permissionForNextStepUpdates = MutableSharedFlow<Unit>()

    override fun sendInfo(info: StepTypedInfo) {
        infoUpdates.tryEmit(info)
    }

    override fun infoUpdates() = infoUpdates

    override fun permissionForNextStepUpdates() =
        permissionForNextStepUpdates
}
