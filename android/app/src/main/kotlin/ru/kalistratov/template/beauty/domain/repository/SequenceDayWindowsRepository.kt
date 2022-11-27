package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek

interface SequenceDayWindowsRepository {
    suspend fun add(window: SequenceDayWindow): SequenceDayWindow?
    suspend fun get(id: Id): SequenceDayWindow?
    suspend fun remove(id: Id): Boolean

    /** @return ids which does`t removed. */
    suspend fun removeAll(ids: List<Id>): List<Id>
}