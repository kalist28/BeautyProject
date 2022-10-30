package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek

interface SequenceDayRepository {
    suspend fun add(day: SequenceDay): SequenceDay?
    suspend fun get(dayNumber: Int): SequenceDay?
    suspend fun getAll(): SequenceWeek
}