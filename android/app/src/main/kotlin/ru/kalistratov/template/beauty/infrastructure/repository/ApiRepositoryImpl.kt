package ru.kalistratov.template.beauty.infrastructure.repository

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkRequestException
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.common.handlingNetworkSafetyWithoutData
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.entity.request.AuthRequest
import ru.kalistratov.template.beauty.domain.entity.request.RefreshTokenRequest
import ru.kalistratov.template.beauty.domain.entity.request.RegistrationRequest
import ru.kalistratov.template.beauty.domain.entity.request.ServerToken
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class ApiRepositoryImpl(
    private val url: String,
    private val authSettingsService: AuthSettingsService
) : ApiRepository {

    companion object {
        private const val AUTH_HEADER = "Authorization"
    }

    private fun getUserId() = authSettingsService.getUserId()
    private fun getBearerToken() = "Bearer ${authSettingsService.getToken()}"
    private fun getRefreshToken() = authSettingsService.getRefreshToken()

    private suspend fun <T> handleUnauthorizedError(obj: suspend () -> T): T = obj.invoke().let {
        if (isUnauthorized(it)) {
            refreshToken().let { result ->
                if (result is NetworkResult.Success) {
                    with(authSettingsService) {
                        val data = result.value
                        updateToken(data.token)
                        updateRefreshToken(data.refreshToken)
                        obj.invoke()
                    }
                } else it
            }
        } else it
    }

    private fun <T> isUnauthorized(obj: T) = obj.let {
        if (it is NetworkResult.GenericError) {
            val error = it.error
            error is NetworkRequestException.RequestException && error.isUnauthorized()
        } else false
    }

    override suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<User> =
        getClient().use {
            handlingNetworkSafety {
                it.post("$url/register") {
                    contentType(ContentType.Application.Json)
                    body = request
                }
            }
        }

    override suspend fun loadWorkdaySequence(id: Id): NetworkResult<WorkdaySequence> =
        getClient().use {
            handlingNetworkSafety<WorkdaySequence> {
                it.get("$url/sequences/days/$id") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                }
            }
        }.logIfError()

    override suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerToken> = getClient()
        .use {
            handlingNetworkSafetyWithoutData<ServerToken> {
                it.post("$url/clients/web/login") {
                    contentType(ContentType.Application.Json)
                    body = request
                }
            }
        }
        .logIfError()

    private suspend fun refreshToken(): NetworkResult<ServerToken> = getClient().use {
        handlingNetworkSafetyWithoutData<ServerToken> {
            it.post("$url/clients/web/refresh") {
                contentType(ContentType.Application.Json)
                body = RefreshTokenRequest(getRefreshToken())
            }
        }
    }.logIfError()

    override suspend fun loadWeekSequence(): NetworkResult<WeekSequence> = handleUnauthorizedError {
        getClient().use {
            handlingNetworkSafetyWithoutData<WeekSequence> {
                it.get("$url/sequences/week") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                }
            }
        }
    }.logIfError()

    override suspend fun updateWorkDaySequence(
        workdaySequence: WorkdaySequence
    ): NetworkResult<WorkdaySequence> = getClient().use {
        handlingNetworkSafety<WorkdaySequence> {
            it.patch("$url/sequences/days/${workdaySequence.day.index}") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = workdaySequence
            }
        }
    }.logIfError()

    override suspend fun createWorkDaySequence(
        workdaySequence: WorkdaySequence
    ): NetworkResult<WorkdaySequence> = getClient().use {
        handlingNetworkSafety<WorkdaySequence> {
            it.post("$url/sequences/days") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = workdaySequence
            }
        }
    }.logIfError()

    override suspend fun createWorkdayWindow(
        workdayWindow: WorkdayWindow
    ): NetworkResult<WorkdayWindow> = getClient().use {
        handlingNetworkSafety<WorkdayWindow> {
            it.post("$url/workday-windows") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = workdayWindow
            }
        }
    }.logIfError()

    override suspend fun updateWorkdayWindow(
        workdayWindow: WorkdayWindow
    ): NetworkResult<WorkdayWindow> = getClient().use {
        handlingNetworkSafety<WorkdayWindow> {
            it.patch("$url/workday-windows/${workdayWindow.id}") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = workdayWindow
            }
        }
    }.logIfError()

    override suspend fun getUser(
        id: String?
    ): NetworkResult<User> = getClient().use {
        handlingNetworkSafety<User> {
            val path = if (id == null) "$url/user/profile" else "$url/users/$id"
            it.get(path) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }.logIfError()

    override suspend fun getWindows(): NetworkResult<List<WorkdayWindow>> = getClient().use {
        handlingNetworkSafety<List<WorkdayWindow>> {
            it.get("$url/workday-windows") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }.logIfError()
}
