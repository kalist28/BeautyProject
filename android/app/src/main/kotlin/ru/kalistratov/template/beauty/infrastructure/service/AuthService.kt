package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.request.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.request.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.request.ServerToken
import ru.kalistratov.template.beauty.domain.extension.doIfSuccess
import ru.kalistratov.template.beauty.interfaces.server.service.ApiAuthService
import ru.kalistratov.template.beauty.interfaces.server.service.ApiUserService
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class AuthServiceImpl(
    private val apiAuthService: ApiAuthService,
    private val apiUserService: ApiUserService,
    private val authSettingsService: AuthSettingsService
) : AuthService {

    override suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<User> = apiAuthService
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
    ): NetworkResult<ServerToken> = apiAuthService
        .auth(request)
        .also { response ->
            response.doIfSuccess {
                with(authSettingsService) {
                    updateToken(it.accessToken)
                    updateRefreshToken(it.refreshToken)

                    apiUserService.getUser()
                        .doIfSuccess { user -> updateUserId(user.id) }
                }
            }
        }
}
