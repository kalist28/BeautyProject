package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek

interface SequenceDayWindowsRepository {
    suspend fun add(window: SequenceDayWindow): SequenceDayWindow?
    suspend fun get(id: Id): SequenceDayWindow?
}