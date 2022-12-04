package ru.kalistratov.template.beauty.infrastructure.repository

import com.soywiz.klock.DateTime
import com.soywiz.klock.Time
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.interfaces.server.service.ApiSequenceService
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toServer

class SequenceDayRepositoryImpl(
    private val apiSequenceService: ApiSequenceService
) : SequenceDayRepository {

    companion object {
        private const val MINUTE_TIMESTAMP = 1 * 60 * 1000
    }

    private var lastCashUpdatedTime = DateTime.now()
    private var cache: SequenceWeek = emptyList()

    private fun needLoad(): Boolean {
        val timeNow = DateTime.now()
        val needLoad = (lastCashUpdatedTime - timeNow).milliseconds > MINUTE_TIMESTAMP
        if (needLoad) lastCashUpdatedTime = timeNow
        return needLoad
    }

    override suspend fun add(day: SequenceDay): SequenceDay? {
        val serverDay = day.toServer()
        return when (serverDay.id.isNotBlank()) {
            true -> apiSequenceService.updateDay(serverDay)
            else -> apiSequenceService.createDay(serverDay)
        }.let { result ->
            when (result is NetworkResult.Success) {
                true -> result.value.toLocal()
                    .also { updateCache(it) }
                false -> null
            }
        }
    }

    override suspend fun get(dayNumber: Int) = loadDay(dayNumber)


    private suspend fun loadDay(dayNumber: Int) = when (
        val result = apiSequenceService.getDay(dayNumber)
    ) {
        is NetworkResult.Success -> result.value
            .toLocal().also(::updateCache)
        else -> null
    }

    override suspend fun getAll(): SequenceWeek {
        return when (val result = apiSequenceService.getWeek()) {
            is NetworkResult.Success -> result.value.days
                .map { it.toLocal() }
                .also { cache = it }
            else -> emptyList()
        }
    }

    private fun updateCache(day: SequenceDay) {
        cache = cache.toMutableList().apply {
            find { it.day == day.day }
                ?.let {
                    val oldIndex = indexOf(it)
                    removeAt(oldIndex)
                    add(oldIndex, day)
                }
        }
    }
}