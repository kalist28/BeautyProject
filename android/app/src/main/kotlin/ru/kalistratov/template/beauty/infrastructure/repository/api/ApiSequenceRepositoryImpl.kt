package ru.kalistratov.template.beauty.infrastructure.repository.api

import io.ktor.client.request.*
import io.ktor.http.*
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.common.handlingNetworkSafety
import ru.kalistratov.template.beauty.common.handlingNetworkSafetyWithoutData
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.extension.getClient
import ru.kalistratov.template.beauty.domain.extension.logIfError
import ru.kalistratov.template.beauty.domain.repository.api.ApiSequenceRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService

class ApiSequenceRepositoryImpl(
    url: String,
    authSettingsService: AuthSettingsService
) : ApiRepository(url, authSettingsService), ApiSequenceRepository {

    private val sequenceUrl = "$url/timetable/sequence-days"

    override suspend fun getDay(
        id: Id
    ): NetworkResult<SequenceDay> =
        getClient().use {
            handlingNetworkSafety<SequenceDay> {
                it.get("$url/sequences/days/$id") {
                    contentType(ContentType.Application.Json)
                    header(AUTH_HEADER, getBearerToken())
                }
            }
        }.logIfError()


    override suspend fun getWeek(): NetworkResult<SequenceWeek> =
        handleUnauthorizedError {
            getClient().use {
                handlingNetworkSafetyWithoutData<SequenceWeek> {
                    it.get(sequenceUrl) {
                        contentType(ContentType.Application.Json)
                        header(AUTH_HEADER, getBearerToken())
                    }
                }
            }
        }.logIfError()

    override suspend fun createDay(
        day: SequenceDay
    ): NetworkResult<SequenceDay> = getClient().use {
        handlingNetworkSafety<SequenceDay> {
            it.post(sequenceUrl) {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = day
            }
        }
    }.logIfError()

    override suspend fun updateDay(
        day: SequenceDay
    ): NetworkResult<SequenceDay> = getClient().use {
        handlingNetworkSafety<SequenceDay> {
            it.patch("$sequenceUrl/${day.day.index}") {
                contentType(ContentType.Application.Json)
                header(AUTH_HEADER, getBearerToken())
                body = day
            }
        }
    }.logIfError()
}
