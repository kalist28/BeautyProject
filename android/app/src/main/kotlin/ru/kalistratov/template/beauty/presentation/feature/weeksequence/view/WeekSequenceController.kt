package ru.kalistratov.template.beauty.presentation.feature.weeksequence.view

import android.content.res.Resources
import android.view.View
import com.airbnb.epoxy.EpoxyController
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.divider
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.domain.entity.TimeSourceType
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.toMilliseconds
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.SwitchModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.TimeRangeViewModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.sequence.WeekSequenceDayModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.setBaseMargins
import ru.kalistratov.template.beauty.titleWithArrow

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

            divider {
                id("divider_1")
                onBind { _, holder, _ -> holder.setBaseMargins() }
            }

            SwitchModel(
                selectedDay.day.index.toString(),
                resources.getString(R.string.workday_holiday),
                selectedDay.isHoliday,
                isHolidayClicks::tryEmit,
                marginsBundle = MarginsBundle.baseHorizontal
            ).addTo(this)

            divider {
                id("divider_2")
                onBind { _, holder, _ -> holder.setBaseMargins() }
            }

            val clickListener = View.OnClickListener {
                editWindowsClicks.tryEmit(selectedDay.day.index)
            }
            val titleForTitleWithArrow = resources.getString(R.string.edit_work_windows)
            titleWithArrow {
                id("windows")
                text(titleForTitleWithArrow)
                onBind { _, holder, _ ->
                    holder.setBaseMargins()
                    holder.dataBinding.root.setOnClickListener(clickListener)
                }
            }

            divider {
                id("divider_3")
                onBind { _, holder, _ -> holder.setBaseMargins() }
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
            marginsBundle = MarginsBundle.baseHorizontal,
            errorMessageChecker = errorMessageChecker,
            startHintId = R.string.workday_start,
            finishHintId = R.string.workday_finish
        )
    }
}
