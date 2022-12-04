package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows

import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.domain.repository.SequenceDayWindowsRepository
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import ru.kalistratov.template.beauty.infrastructure.extensions.plus

class EditSequenceDayWindowsInteractorImpl(
    private val sequenceDayRepository: SequenceDayRepository,
    private val sequenceDayWindowsRepository: SequenceDayWindowsRepository,
) : EditWorkdayWindowsInteractor {

    private val sortWindowsComparator = Comparator<SequenceDayWindow> { day1, day2 ->
        day1.startAt.compareTo(day2.startAt)
    }

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

    override suspend fun createNewWindow(day: SequenceDay): SequenceDayWindow {
        val dayTimeRange = day.let { TimeRange(it.startAt, it.finishAt) }
        val timePair = day.windows.lastOrNull()
            ?.let { it.finishAt to it.finishAt.plus(hour = 1) }
            ?: dayTimeRange.start.let { it to it.plus(hour = 1) }
        return SequenceDayWindow(
            id = "",
            sequenceDayId = day.id,
            startAt = timePair.first,
            finishAt = timePair.second
        )
    }

    override suspend fun updateWindowList(
        window: SequenceDayWindow,
        windows: List<SequenceDayWindow>
    ): List<SequenceDayWindow> = windows.toMutableList().let { list ->
        val dayIndex = list.indexOfFirst { it.id == window.id }
        if (dayIndex >= 0) {
            list.removeAt(dayIndex)
            list.add(dayIndex, window)
        } else list.add(window)
        list.sortedWith(sortWindowsComparator)
    }

}
