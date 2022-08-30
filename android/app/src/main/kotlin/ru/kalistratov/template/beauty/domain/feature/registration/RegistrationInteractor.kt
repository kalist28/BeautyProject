package ru.kalistratov.template.beauty.domain.feature.registration

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.domain.entity.request.AuthResult
import ru.kalistratov.template.beauty.domain.entity.request.RegistrationRequest
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepTypedInfo

interface RegistrationInteractor {
    fun stepInfoChanges(): Flow<StepTypedInfo>

    suspend fun registration(request: RegistrationRequest): AuthResult
    suspend fun requestLoadUser()
}
