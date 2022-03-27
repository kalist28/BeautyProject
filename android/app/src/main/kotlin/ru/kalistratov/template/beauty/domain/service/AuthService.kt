package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*

interface AuthService {
    suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<User>

    suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerToken>
}
