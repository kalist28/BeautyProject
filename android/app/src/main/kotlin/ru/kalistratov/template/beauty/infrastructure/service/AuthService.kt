package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.extension.doIfSuccess
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class AuthServiceImpl(
    private val apiRepository: ApiRepository,
    private val authSettingsService: AuthSettingsService
) : AuthService {

    override suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<User> = apiRepository
        .registration(request)
        .also { response ->
            response.doIfSuccess {
                auth(
                    AuthRequest(
                        request.email,
                        request.password
                    )
                )
            }
        }

    override suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerToken> = apiRepository.auth(
        AuthRequest(
            request.email,
            request.password
        )
    ).also { response ->
        response.doIfSuccess {
            authSettingsService.updateToken(it.token)
            authSettingsService.updateRefreshToken(it.refreshToken)

            apiRepository.getData().doIfSuccess { user ->
                authSettingsService.updateUserId(user.id)
            }
        }
    }
}
