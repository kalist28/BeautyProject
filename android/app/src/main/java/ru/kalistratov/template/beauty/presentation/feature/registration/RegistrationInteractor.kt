package ru.kalistratov.template.beauty.presentation.feature.registration

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

interface RegistrationInteractor {
    suspend fun saveUser(name: String, token: String)
    suspend fun registration(request: RegistrationRequest): AuthResult
}

class RegistrationInteractorImpl(
    private val authService: AuthService,
    private val authSettingsService: AuthSettingsService,
) : RegistrationInteractor {

    override suspend fun saveUser(name: String, token: String) =
        authSettingsService.updateUser(name, token)

    override suspend fun registration(request: RegistrationRequest): AuthResult =
        when (val result = authService.registration(request)) {
            is NetworkResult.Success -> AuthResult.Success(result.value)
            is NetworkResult.GenericError -> AuthResult.Error(result.value.exception)
        }
}
