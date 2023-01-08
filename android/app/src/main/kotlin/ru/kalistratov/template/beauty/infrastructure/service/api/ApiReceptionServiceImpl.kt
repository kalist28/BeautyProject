package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.ServerUrl
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.interfaces.server.dto.FreeSequenceDayWindowsRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.FreeSequenceDayWindowsResponse
import ru.kalistratov.template.beauty.interfaces.server.service.ApiReceptionService
import javax.inject.Inject

class ApiReceptionServiceImpl @Inject constructor(
    url: ServerUrl,
    sessionManager: SessionManager,
    authSettingsService: AuthSettingsService
) : ApiService(url, sessionManager, authSettingsService), ApiReceptionService {

    private val receptionUrl = "$url/timetable/reception"

    override suspend fun loadFreeWindowsForDay(
        request: FreeSequenceDayWindowsRequest
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<FreeSequenceDayWindowsResponse> {
            it.get("$receptionUrl/month-day") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = request
                loge("reauest = $request")
            }
        }
    }.logIfError()

    override suspend fun makeReservation() {
        TODO("Not yet implemented")
    }
}