package ru.kalistratov.template.beauty.domain.service

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepTypedInfo

interface RegistrationStepService {
    fun sendInfo(info: StepTypedInfo)
    fun infoUpdates(): Flow<StepTypedInfo>
    fun permissionForNextStepUpdates(): Flow<Unit>
}