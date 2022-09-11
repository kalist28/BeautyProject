package ru.kalistratov.template.beauty.infrastructure.repository.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.repository.api.ApiWorkdayWindowRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class ApiWorkdayWindowRepositoryImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiRepository(url, authSettingsService), ApiWorkdayWindowRepository {

    override suspend fun get(
        dayNumber: Int
    ): NetworkResult<SequenceDay> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<SequenceDay> {
                it.get("$url/sequences/days/$dayNumber") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                }
            }
        }.logIfError()

    override suspend fun getAll(): NetworkResult<List<WorkdayWindow>> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<List<WorkdayWindow>> {
                it.get("$url/workday-windows") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                }
            }
        }.logIfError()

    override suspend fun create(
        window: WorkdayWindow
    ): NetworkResult<WorkdayWindow> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<WorkdayWindow> {
                it.post("$url/workday-windows") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                    body = window
                }
            }
        }.logIfError()

    override suspend fun update(
        window: WorkdayWindow
    ): NetworkResult<WorkdayWindow> = getClient()
        .useWithHandleUnauthorizedError {
            handlingNetworkSafety<WorkdayWindow> {
                it.patch("$url/workday-windows/${window.id}") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                    body = window
                }
            }
        }.logIfError()
}
