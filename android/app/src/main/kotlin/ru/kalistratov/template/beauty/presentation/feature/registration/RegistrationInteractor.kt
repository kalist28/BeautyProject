package ru.kalistratov.template.beauty.presentation.feature.registration

import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.domain.feature.registration.RegistrationInteractor
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.RegistrationStepService
import ru.kalistratov.template.beauty.presentation.feature.registration.entity.StepTypedInfo

class RegistrationInteractorImpl(
    private val authService: AuthService,
    private val registrationStepService: RegistrationStepService
) : RegistrationInteractor {

    override suspend fun registration(request: RegistrationRequest): AuthResult =
        when (val result = authService.registration(request)) {
            is NetworkResult.Success -> AuthResult.Success
            is NetworkResult.GenericError -> AuthResult.Error(result.error.exception)
        }

    override fun stepInfoChanges(): Flow<StepTypedInfo> =
        registrationStepService.infoUpdates()
}
