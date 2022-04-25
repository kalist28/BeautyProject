package ru.kalistratov.template.beauty.infrastructure.repository

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkRequestException
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.common.handlingNetworkSafetyWithoutData
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class ApiRepositoryImpl(
    private val url: String,
    private val authSettingsService: AuthSettingsService
) : ApiRepository {

    companion object {
        private const val AUTH_HEADER = "Authorization"
    }

    private fun getUserId() = "Bearer ${authSettingsService.getUserId()}"
    private fun getBearerToken() = "Bearer ${authSettingsService.getToken()}"

    private fun <T> handleUnauthorizedError(obj: T): T = obj.also {
        if (it is NetworkResult.GenericError) {
            val error = it.error
            if (error is NetworkRequestException.RequestException && error.isUnauthorized()) {
                throw error.exception
            }
        }
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

    override suspend fun auth(
        request: AuthRequest
    ): NetworkResult<ServerToken> =
        getClient().use {
            handlingNetworkSafetyWithoutData {
                it.post("$url/clients/web/login") {
                    contentType(ContentType.Application.Json)
                    body = request
                }
            }
        }

    override suspend fun loadWeekSequence(): NetworkResult<WeekSequence> = getClient().use {
        handlingNetworkSafetyWithoutData {
            it.get("$url/sequences/week") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }

    override suspend fun updateWorkDaySequence(
        workDaySequence: WorkDaySequence
    ): NetworkResult<WorkDaySequence> = getClient().use {
        handlingNetworkSafety {
            it.patch("$url/sequences/days/${workDaySequence.day.index}") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = workDaySequence
            }
        }
    }

    override suspend fun createWorkDaySequence(
        workDaySequence: WorkDaySequence
    ): NetworkResult<WorkDaySequence> = getClient().use {
        handlingNetworkSafety {
            it.post("$url/sequences/days") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = workDaySequence
            }
        }
    }

    override suspend fun getUser(
        id: String
    ): NetworkResult<User> = getClient().use {
        handlingNetworkSafety {
            it.post("$url/user-week-sequence/create/$id") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }
}
