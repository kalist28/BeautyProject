package ru.kalistratov.template.beauty.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.CalendarOneLineBinding
import ru.kalistratov.template.beauty.presentation.extension.setTextColorRes
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class OneLineCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: CalendarOneLineBinding

    protected val today = LocalDate.now()
    protected val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    var selectedDay: WeekDay? = null
        set(value) {
            val lastValue = field
            field = value
            /*with(binding.calendarView) {
                lastValue?.let(::notifyDayChanged)
                field?.let(::notifyDayChanged)
            }*/
        }

    var onDayClickAction: ((CalendarDay) -> Unit)? = null

    init {
        inflate(context, R.layout.calendar_one_line, this)
        binding = CalendarOneLineBinding.bind(this)
        initCalendarBinding()
    }

    private fun initCalendarBinding() = with(binding.calendarView) {
        dayBinder = object : WeekDayBinder<OneLineDayViewContainer> {
            override fun bind(container: OneLineDayViewContainer, day: WeekDay) {
                container.day = day

                val isCurrent = today == day.date
                val isThisMonth = day.date.month == today.month
                val isSelected = selectedDay != null && selectedDay!!.date == day.date

                val textView = container.dayText
                textView.text = day.date.dayOfMonth.toString()
                container.clickable = isThisMonth

                if (isThisMonth) when {
                    isSelected -> {
                        textView.setTextColorRes(R.color.surface)
                        textView.setBackgroundResource(R.drawable.background_calendar_day_selected)
                    }
                    isCurrent -> {
                        textView.setTextColorRes(R.color.onSurface)
                        textView.setBackgroundResource(R.drawable.background_calendar_day_current)
                    }
                    else -> {
                        textView.setTextColorRes(R.color.onSurface)
                        textView.background = null
                    }
                } else {
                    textView.setTextColorRes(R.color.surfaceVariant)
                    textView.background = null
                }
            }

            override fun create(view: View) = OneLineDayViewContainer(view) {
                //onDayClickAction?.invoke(it)
                selectedDay = it
            }
        }

        weekScrollListener = {
            binding.yearText.text = "2002"
            binding.monthText.text = "monthTitleFormatter.format(it.yearMonth)"
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(30)
        val lastMonth = currentMonth.plusMonths(20)
       // setup(firstMonth, lastMonth, DayOfWeek.MONDAY)
        //scrollPaged = true
        scrollToDate(today)

        /* updateMonth(
             inDateStyle = InDateStyle.ALL_MONTHS,
             outDateStyle = OutDateStyle.END_OF_ROW,
             maxRowCount = 1,
             hasBoundaries = false
         ) {
             scrollToMonth(YearMonth.now())
             scrollToDate(today)
         }*/
    }
}

class OneLineDayViewContainer(
    view: View,
    clickAction: (WeekDay) -> Unit
) : ViewContainer(view) {
    var day: WeekDay? = null
    val dayText: TextView = view.findViewById(R.id.day)
    var clickable: Boolean = false

    init {
        view.setOnClickListener {
            if (clickable) day?.let(clickAction::invoke)
        }
    }
}
