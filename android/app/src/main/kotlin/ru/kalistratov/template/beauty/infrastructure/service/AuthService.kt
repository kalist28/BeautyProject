package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.ServerAuthResult
import ru.kalistratov.template.beauty.domain.repository.ApiRepository
import ru.kalistratov.template.beauty.domain.service.AuthService

class AuthServiceImpl(private val apiRepository: ApiRepository) : AuthService {

    override suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<ServerAuthResult> = apiRepository.registration(request)

    override suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerAuthResult> = apiRepository.auth(request)
}
