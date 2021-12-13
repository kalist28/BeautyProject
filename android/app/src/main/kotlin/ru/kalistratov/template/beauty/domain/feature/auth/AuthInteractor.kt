package ru.kalistratov.template.beauty.domain.feature.auth

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

interface AuthInteractor {
    suspend fun saveUser(name: String, token: String)
    suspend fun auth(request: AuthRequest): AuthResult
}