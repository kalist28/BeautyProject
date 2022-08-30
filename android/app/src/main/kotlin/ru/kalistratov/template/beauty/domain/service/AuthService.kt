package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.request.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.request.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.request.ServerToken

interface AuthService {
    suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<User>

    suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerToken>
}
