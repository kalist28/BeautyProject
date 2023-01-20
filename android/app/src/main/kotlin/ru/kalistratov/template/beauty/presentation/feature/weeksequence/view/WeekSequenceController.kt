package ru.kalistratov.template.beauty.presentation.feature.weeksequence.view

import android.content.res.Resources
import com.airbnb.epoxy.EpoxyController
import ru.kalistratov.template.beauty.*
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.entity.TimeSourceType
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.toMilliseconds
import ru.kalistratov.template.beauty.presentation.extension.setDebouncedOnClickListener
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.SwitchModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.TimeRangeViewModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.sequence.WeekSequenceDayModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins

class WeekSequenceController(
    private val resources: Resources
) : EpoxyController() {

    val clicks = mutableSharedFlow<Int>()
    val dayTimeClicks = mutableSharedFlow<TimeSourceType>()
    val isHolidayClicks = mutableSharedFlow<Boolean>()
    val editWindowsClicks = mutableSharedFlow<Int>()

    var sequenceWeek: SequenceWeek = emptyList()
    var selectedDay: SequenceDay? = null

    override fun buildModels() {
        val selectedDay = selectedDay
        if (selectedDay == null) sequenceWeek.forEach {
            add(WeekSequenceDayModel(it, clicks::tryEmit))
        } else {
            createTimeRangeViewModel(selectedDay).addTo(this)

            SwitchModel(
                selectedDay.day.index.toString(),
                resources.getString(R.string.workday_holiday),
                selectedDay.isHoliday,
                isHolidayClicks::tryEmit,
                marginsBundle = MarginsBundle.baseHorizontal
            ).addTo(this)

            indentSmall {
                id("indentSmall_1")
            }

            val titleForTitleWithArrow = resources.getString(R.string.edit_work_windows)
            val clickListener: () -> Unit = {
                editWindowsClicks.tryEmit(selectedDay.day.index)
            }
            titleWithArrow {
                id("windows")
                text(titleForTitleWithArrow)
                onBind { _, holder, _ ->
                    holder.dataBinding.root.setDebouncedOnClickListener(block = clickListener)
                }
            }
        }
    }

    private fun createTimeRangeViewModel(day: SequenceDay): TimeRangeViewModel {
        val startTime = day.timeSource(TimeSourceType.START_KEY)
        val finishTime = day.timeSource(TimeSourceType.FINISH_KEY)
        val errorMessageChecker: () -> String? = {
            val startMills = startTime.time.toMilliseconds()
            val finishMills = finishTime.time.toMilliseconds()
            val inverseRange = startMills > finishMills

            if (inverseRange) resources.getString(R.string.inverse_range)
            else null
        }
        return TimeRangeViewModel(
            day.day.index.toString(),
            startTime,
            finishTime,
            dayTimeClicks::tryEmit,
            marginsBundle = MarginsBundle.smallVertical,
            errorMessageChecker = errorMessageChecker,
            startHintId = R.string.workday_start,
            finishHintId = R.string.workday_finish
        )
    }
}
