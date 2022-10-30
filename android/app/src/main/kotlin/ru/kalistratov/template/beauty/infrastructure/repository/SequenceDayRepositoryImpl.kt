package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.domain.service.api.ApiSequenceService
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toLocal
import ru.kalistratov.template.beauty.infrastructure.helper.mapper.toServer

class SequenceDayRepositoryImpl(
    private val apiSequenceService: ApiSequenceService
) : SequenceDayRepository {

    private var cache: SequenceWeek = emptyList()

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

    override suspend fun get(dayNumber: Int): SequenceDay? {
        return cache.find { it.day.index == dayNumber }
            ?: when (val result = apiSequenceService.getDay(dayNumber)) {
                is NetworkResult.Success -> result.value.toLocal()
                else -> null
            }
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
            find { it.id == day.id }
                ?.let {
                    val oldIndex = indexOf(it)
                    add(oldIndex, day)
                }
        }
    }
}