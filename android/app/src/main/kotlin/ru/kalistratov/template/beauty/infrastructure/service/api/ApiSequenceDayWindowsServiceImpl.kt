package ru.kalistratov.template.beauty.infrastructure.service.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.api.ApiSequenceDayWindowsService
import ru.kalistratov.template.beauty.infrastructure.entity.dto.ServerSequenceDayWindow

class ApiSequenceDayWindowsServiceImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiService(url, authSettingsService), ApiSequenceDayWindowsService {

    private val windowsUrl = "$url/timetable/windows"

    override suspend fun get(
        id: Id
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerSequenceDayWindow> {
            it.get("$windowsUrl/$id") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
            }
        }
    }.logIfError()

    override suspend fun create(
        window: ServerSequenceDayWindow
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerSequenceDayWindow> {
            it.post(windowsUrl) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = window
            }
        }
    }.logIfError()

    override suspend fun update(
        window: ServerSequenceDayWindow
    ) = getClient().useWithHandleUnauthorizedError {
        handlingNetworkSafety<ServerSequenceDayWindow> {
            it.patch("$windowsUrl/${window.id}") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = window
            }
        }
    }.logIfError()
}
