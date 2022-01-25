package ru.kalistratov.template.beauty.presentation.feature.auth

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.feature.auth.AuthInteractor
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class AuthInteractorImpl(
    private val authService: AuthService,
    private val authSettingsService: AuthSettingsService,
) : AuthInteractor {

    override suspend fun saveUser(name: String, token: String) =
        authSettingsService.updateUser(name, token)

    override suspend fun auth(request: AuthRequest): AuthResult =
        when (val result = authService.auth(request)) {
            is NetworkResult.Success -> AuthResult.Success(result.value)
            is NetworkResult.GenericError -> AuthResult.Error(result.value.exception)
        }
}
