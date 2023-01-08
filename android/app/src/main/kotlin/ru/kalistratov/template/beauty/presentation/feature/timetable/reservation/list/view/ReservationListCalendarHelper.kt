package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.view

import android.view.View
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.CalendarDayBinding
import ru.kalistratov.template.beauty.domain.entity.SequenceWeek
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.displayText
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class ReservationListCalendarHelper {

    private val dayClicksMutableFlow = mutableSharedFlow<LocalDate>()
    private var selectedDay: LocalDate? = null

    var sequenceWeek: SequenceWeek? = null
    var calendar: WeekCalendarView? = null

    fun initialize(calendar: WeekCalendarView) = with(calendar) {
        this@ReservationListCalendarHelper.calendar = calendar
        dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view) {
                dayClicksMutableFlow.tryEmit(it.date)
            }

            override fun bind(container: DayViewContainer, data: WeekDay) {
                val dayState = getDayState(data.date)
                val isHoliday = if (sequenceWeek.isNullOrEmpty()) false
                else sequenceWeek?.get(data.date.dayOfWeek.value - 1)
                    ?.run { isHoliday || isNotExist() } ?: false
                container.bind(data, dayState, isHoliday)
            }
        }

        val currentMonth = YearMonth.now()
        setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )
        scrollToDate(LocalDate.now())
    }

    fun dayClicks() = dayClicksMutableFlow.asSharedFlow()

    fun render(selectedDay: LocalDate) {
        if (sequenceWeek == null || this.selectedDay == selectedDay) return
        val oldSelectedDay = this.selectedDay
        this.selectedDay = selectedDay
        calendar?.apply {
            oldSelectedDay?.let(::notifyDateChanged)
            notifyDateChanged(selectedDay)
            scrollToDate(selectedDay)
        }
    }

    private fun getDayState(date: LocalDate): DayState = when {
        isSelectedDate(date) -> DayState.SELECTED
        isCurrentDate(date) -> DayState.CURRENT
        else -> DayState.EMPTY
    }.also { loge("$date to $it") }

    private fun isCurrentDate(date: LocalDate) = LocalDate.now() == date
    private fun isSelectedDate(date: LocalDate) = selectedDay?.let { it == date } ?: false
}

enum class DayState {
    EMPTY, CURRENT, SELECTED
}

class DayViewContainer(
    view: View, clickAction: (WeekDay) -> Unit
) : ViewContainer(view) {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd")
    }

    val bind = CalendarDayBinding.bind(view)
    var day: WeekDay? = null

    init {
        view.setOnClickListener {
            day?.let(clickAction::invoke)
        }
    }

    fun bind(day: WeekDay, state: DayState, isHoliday: Boolean) {
        this.day = day
        bind.number.apply {
            text = dateFormatter.format(day.date)

            when (state) {
                DayState.CURRENT -> R.drawable.background_calendar_day_current
                DayState.SELECTED -> R.drawable.background_calendar_day_selected
                DayState.EMPTY -> null
            }.let { resId ->
                if (resId == null) background = null
                else setBackgroundResource(resId)
            }

            setTextColor(
                context.getColor(
                    when {
                        state == DayState.SELECTED -> R.color.surface
                        isHoliday -> R.color.outline
                        else -> R.color.onSurface
                    }
                )
            )
        }
        bind.day.text = day.date.dayOfWeek.displayText()
    }
}