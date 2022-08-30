package ru.kalistratov.template.beauty.presentation.feature.auth

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.request.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.request.AuthResult
import ru.kalistratov.template.beauty.domain.feature.auth.AuthInteractor
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.service.AuthService

class AuthInteractorImpl(
    private val authService: AuthService,
    private val userRepository: UserRepository
) : AuthInteractor {

    override suspend fun auth(request: AuthRequest): AuthResult =
        when (val result = authService.auth(request)) {
            is NetworkResult.Success -> AuthResult.Success
            is NetworkResult.GenericError -> AuthResult.Error(result.error.exception)
        }

    override suspend fun requestLoadUser() =
        userRepository.requestLoad()
}
