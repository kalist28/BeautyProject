package ru.kalistratov.template.beauty.infrastructure.repository

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.repository.ApiRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class ApiRepositoryImpl(
    private val url: String,
    private val authSettingsService: AuthSettingsService
) : ApiRepository {

    companion object {
        private const val AUTH_HEADER = "Authorization"
    }

    private fun getBearerToken() = "Bearer ${authSettingsService.loadToken()}"

    override suspend fun registration(
        request: RegistrationRequest
    ): NetworkResult<ServerAuthResult> =
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
    ): NetworkResult<ServerAuthResult> =
        getClient().use {
            handlingNetworkSafety {
                it.post("$url/login") {
                    contentType(ContentType.Application.Json)
                    body = request
                }
            }
        }

    override suspend fun loadWeekSequence(): NetworkResult<WeekSequence> = getClient().use {
        handlingNetworkSafety {
            it.get("$url/user-week-sequence/list") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }

    override suspend fun updateWorkDaySequence(
        workDaySequence: WorkDaySequence
    ): NetworkResult<Unit> = getClient().use {
        loge("ppp - ${jsonParser.encodeToString(workDaySequence)}")
        handlingNetworkSafety {
            it.post("$url/user-week-sequence/create/${workDaySequence.day.index}") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = workDaySequence
            }
        }
    }
}
