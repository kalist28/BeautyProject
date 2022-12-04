package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.common.handlingNetworkSafetyWithoutData
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.interfaces.server.service.ApiSequenceService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerSequenceDay
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerSequenceWeek

class ApiSequenceServiceImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiService(url, authSettingsService), ApiSequenceService {

    private val sequenceUrl = "$url/timetable/sequence-days"

    override suspend fun getDay(
        dayNumber: Int
    ): NetworkResult<ServerSequenceDay> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<ServerSequenceDay> {
                it.get("$sequenceUrl/$dayNumber") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                }
            }
        }.logIfError()


    override suspend fun getWeek(): NetworkResult<ServerSequenceWeek> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafetyWithoutData<ServerSequenceWeek> {
                it.get(sequenceUrl) {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                }
            }
        }.logIfError()

    override suspend fun createDay(
        day: ServerSequenceDay
    ): NetworkResult<ServerSequenceDay> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<ServerSequenceDay> {
                it.post(sequenceUrl) {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                    body = day
                }
            }
        }.logIfError()

    override suspend fun updateDay(
        day: ServerSequenceDay
    ): NetworkResult<ServerSequenceDay> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<ServerSequenceDay> {
                it.patch("$sequenceUrl/${day.day.index}") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                    body = day
                }
            }
        }.logIfError()
}
