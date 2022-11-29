package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.domain.repository.SequenceDayWindowsRepository

class EditSequenceDayWindowsInteractorImpl(
    private val sequenceDayRepository: SequenceDayRepository,
    private val sequenceDayWindowsRepository: SequenceDayWindowsRepository,
) : EditWorkdayWindowsInteractor {

    override suspend fun getSequenceDay(dayNumber: Int): SequenceDay? =
        sequenceDayRepository.get(dayNumber)

    override suspend fun pushWindow(
        window: SequenceDayWindow
    ) = sequenceDayWindowsRepository.add(window)

    override suspend fun removeWindows(
        ids: List<Id>,
        day: SequenceDay
    ): SequenceDay {
        val unmovedIds = sequenceDayWindowsRepository.removeAll(ids)
        val removedIds = ids.filter { it !in unmovedIds }
        val windows = day.windows.filter { it.id !in removedIds }
        return day.copy(windows = windows)
    }
}
