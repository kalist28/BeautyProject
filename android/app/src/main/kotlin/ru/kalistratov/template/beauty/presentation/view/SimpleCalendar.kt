package ru.kalistratov.template.beauty.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.presentation.extension.setTextColorRes
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class SimpleCalendar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    protected val yearText: TextView by lazy { findViewById(R.id.year_text) }
    protected val monthText: TextView by lazy { findViewById(R.id.month_text) }
    protected val calendar: CalendarView by lazy { findViewById(R.id.calendar_view) }

    protected val today = LocalDate.now()
    protected val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    init {
        inflate(context, R.layout.calendar_simple, this)

        initCalendarBinding()
    }

    private fun initCalendarBinding() {

        calendar.dayBinder = object : DayBinder<SimpleDayViewContainer> {
            override fun create(view: View) = SimpleDayViewContainer(view)
            override fun bind(container: SimpleDayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.dayText
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    when {
                        today == day.date -> {
                            textView.setTextColorRes(R.color.black)
                            textView.setBackgroundResource(R.drawable.calendar_day_current)
                        }
                        else -> {
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                        }
                    }
                } else {
                    textView.setTextColorRes(R.color.lightDark)
                    textView.background = null
                }
            }
        }

        calendar.monthScrollListener = {
            yearText.text = it.yearMonth.year.toString()
            monthText.text = monthTitleFormatter.format(it.yearMonth)
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(30)
        val lastMonth = currentMonth.plusMonths(20)
        calendar.setup(firstMonth, lastMonth, DayOfWeek.MONDAY)
        calendar.scrollToMonth(currentMonth)
        calendar.updateMonthConfiguration(
            inDateStyle = InDateStyle.ALL_MONTHS,
            maxRowCount = 6,
            hasBoundaries = true
        )
    }
}

class SimpleDayViewContainer(view: View) : ViewContainer(view) {
    var day: CalendarDay? = null
    val dayText = view.findViewById<TextView>(R.id.day_text)

    init {
        view.setOnClickListener {
            /*if (day.owner == DayOwner.THIS_MONTH) {
                if (dayText.contains(day.date)) {
                    selectedDates.remove(day.date)
                } else {
                    selectedDates.add(day.date)
                }
                calendar.notifyDayChanged(day)
            }*/
        }
    }
}
