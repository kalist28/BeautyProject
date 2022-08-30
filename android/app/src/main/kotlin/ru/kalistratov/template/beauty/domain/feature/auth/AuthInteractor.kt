package ru.kalistratov.template.beauty.domain.feature.auth

import ru.kalistratov.template.beauty.domain.entity.request.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.request.AuthResult

interface AuthInteractor {
    suspend fun auth(request: AuthRequest): AuthResult
    suspend fun requestLoadUser()
}
