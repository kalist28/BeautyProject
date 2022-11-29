package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkRequestException
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafetyWithoutData
import ru.kalistratov.template.beauty.domain.entity.request.RefreshTokenRequest
import ru.kalistratov.template.beauty.domain.entity.request.ServerToken
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

abstract class ApiService(
    protected val url: String,
    protected val authSettingsService: AuthSettingsService
) {

    companion object {
        @JvmStatic
        protected val AUTH_HEADER = "Authorization"
    }

    protected fun getUserId() = authSettingsService.getUserId()
    protected fun getBearerToken() = "Bearer ${authSettingsService.getToken()}"
    protected fun getRefreshToken() = authSettingsService.getRefreshToken()

    protected suspend fun <T> handleUnauthorizedError(obj: suspend () -> T): T = obj.invoke().let {
        if (isUnauthorized(it)) {
            refreshToken().let { result ->
                if (result is NetworkResult.Success) {
                    with(authSettingsService) {
                        val data = result.value
                        updateToken(data.accessToken)
                        updateRefreshToken(data.refreshToken)
                        obj.invoke()
                    }
                } else it
            }
        } else it
    }

    protected suspend inline fun <T> HttpClient.useWithHandleUnauthorizedError(
        obj: (HttpClient) -> T
    ): T = this.use {
        obj.invoke(this).let {
            if (isUnauthorized(it)) refreshToken()
                .let { result ->
                    if (result is NetworkResult.Success) {
                        updateToken(result.value)
                        obj.invoke(this)
                    } else it
                }
            else it
        }
    }

    protected fun <T> isUnauthorized(obj: T) = obj.let {
        if (it is NetworkResult.GenericError) {
            val error = it.error
            error is NetworkRequestException.RequestException && error.isUnauthorized()
        } else false
    }

    protected fun updateToken(token: ServerToken) =
        with(authSettingsService) {
            updateToken(token.accessToken)
            updateRefreshToken(token.refreshToken)
        }

    protected suspend fun refreshToken(): NetworkResult<ServerToken> = getClient().use {
        handlingNetworkSafetyWithoutData<ServerToken> {
            it.post("$url/clients/web/refresh") {
                contentType(ContentType.Application.Json)
                body = RefreshTokenRequest(getRefreshToken())
            }
        }
    }.logIfError()
}
