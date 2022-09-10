package ru.kalistratov.template.beauty.presentation.view.weeksequence

import android.view.View
import android.view.ViewParent
import android.widget.TextView
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.extension.isNoTime
import ru.kalistratov.template.beauty.domain.extension.toClockFormat

class WeekSequenceDayModel(
    private val workday: SequenceDay,
    private val clickListener: (Int) -> Unit = {},
) : EpoxyModelWithHolder<WeekSequenceDayModel.WorkDayHolder>() {

    init {
        id(workday.hashCode())
    }

    override fun getDefaultLayout(): Int = R.layout.item_sequence_work_day

    override fun bind(holder: WorkDayHolder) = with(holder) {
        root?.setOnClickListener { clickListener.invoke(workday.day.index) }

        weekDayTextView?.apply {
            text = context?.getString(workday.day.shortTittleResId)
        }
        val start = workday.startAt
        val finish = workday.finishAt
        // TODO replace to REsID
        timeTextView?.text = when {
            workday.isHoliday -> "Выходной"
            start.isNoTime() && finish.isNoTime() -> "Не указано"
            else -> "${start.toClockFormat()} - ${finish.toClockFormat()}"
        }
    }

    override fun createNewHolder(parent: ViewParent) = WorkDayHolder()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as WeekSequenceDayModel

        if (workday != other.workday) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + workday.hashCode()
        return result
    }

    class WorkDayHolder : EpoxyHolder() {
        var root: View? = null
        var timeTextView: TextView? = null
        var weekDayTextView: TextView? = null
        override fun bindView(itemView: View) = with(itemView) {
            root = itemView
            timeTextView = findViewById(R.id.time_text_view)
            weekDayTextView = findViewById(R.id.week_day_text_view)
        }
    }
}
