package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.ServerAuthResult

interface AuthService {
    suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<ServerAuthResult>

    suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerAuthResult>
}
