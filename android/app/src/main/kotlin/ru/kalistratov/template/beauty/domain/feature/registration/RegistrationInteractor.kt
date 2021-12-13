package ru.kalistratov.template.beauty.domain.feature.registration

import ru.kalistratov.template.beauty.domain.entity.AuthResult
import ru.kalistratov.template.beauty.domain.entity.RegistrationRequest

interface RegistrationInteractor {
    suspend fun saveUser(name: String, token: String)
    suspend fun registration(request: RegistrationRequest): AuthResult
}
