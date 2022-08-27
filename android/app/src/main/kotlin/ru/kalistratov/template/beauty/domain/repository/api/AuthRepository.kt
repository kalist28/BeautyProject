package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.ServerToken
import ru.kalistratov.template.beauty.domain.entity.User

interface AuthRepository {
    suspend fun auth(request: AuthRequest): NetworkResult<ServerToken>
    suspend fun registration(request: RegistrationRequest): NetworkResult<User>
}