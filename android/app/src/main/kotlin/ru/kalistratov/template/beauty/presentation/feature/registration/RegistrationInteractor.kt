package ru.kalistratov.template.beauty.presentation.feature.registration

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.domain.feature.registration.RegistrationInteractor
import ru.kalistratov.template.beauty.domain.service.AuthService

class RegistrationInteractorImpl(
    private val authService: AuthService
) : RegistrationInteractor {

    override suspend fun registration(request: RegistrationRequest): AuthResult =
        when (val result = authService.registration(request)) {
            is NetworkResult.Success -> AuthResult.Success
            is NetworkResult.GenericError -> AuthResult.Error(result.error.exception)
        }
}
