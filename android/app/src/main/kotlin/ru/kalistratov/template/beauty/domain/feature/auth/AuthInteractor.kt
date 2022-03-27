package ru.kalistratov.template.beauty.domain.feature.auth

import ru.kalistratov.template.beauty.domain.entity.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.AuthResult

interface AuthInteractor {
    suspend fun auth(request: AuthRequest): AuthResult
}
