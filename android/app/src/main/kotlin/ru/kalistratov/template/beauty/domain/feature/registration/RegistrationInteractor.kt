package ru.kalistratov.template.beauty.domain.feature.registration

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepTypedInfo

interface RegistrationInteractor {
    suspend fun registration(request: RegistrationRequest): AuthResult
    fun stepInfoChanges(): Flow<StepTypedInfo>
}
