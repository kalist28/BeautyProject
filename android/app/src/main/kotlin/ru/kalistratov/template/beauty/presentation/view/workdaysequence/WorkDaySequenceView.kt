package ru.kalistratov.template.beauty.presentation.view.workdaysequence

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.extension.isNoTime
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

@SuppressLint("ViewConstructor")
class WorkDaySequenceView @JvmOverloads constructor(
    day: WorkDaySequence,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val timeTextView: TextView by lazy { findViewById(R.id.time_text_view) }
    private val weekDayTextView: TextView by lazy { findViewById(R.id.week_day_text_view) }

    init {
        inflate(context, R.layout.item_sequence_work_day, this)

        weekDayTextView.text = context.getString(day.day.shortTittleResId)
        // TODO replace to REsID
        val text = when {
            day.isHoliday -> "Выходной"
            day.from.isNoTime() && day.to.isNoTime() -> "Не указано"
            else -> "${day.from} - ${day.to}"
        }
        if (text == "Не указано") loge("daaay - $day")
        timeTextView.text = text
    }
}
