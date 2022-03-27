package ru.kalistratov.template.beauty.presentation.view.weeksequence

import android.view.View
import android.view.ViewParent
import android.widget.TextView
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.extension.isNoTime

class WeekSequenceDayModel(
    private val workDay: WorkDaySequence,
    private val clickListener: OnDayClickListener?,
) : EpoxyModelWithHolder<WeekSequenceDayModel.WorkDayHolder>() {

    init {
        id(workDay.hashCode())
    }

    override fun getDefaultLayout(): Int = R.layout.item_sequence_work_day

    override fun bind(holder: WorkDayHolder) = with(holder) {
        root?.setOnClickListener { clickListener?.onDayClick(workDay.day.index) }

        weekDayTextView?.apply {
            text = context?.getString(workDay.day.shortTittleResId)
        }
        val start = workDay.startAt
        val finish = workDay.finishAt
        // TODO replace to REsID
        timeTextView?.text = when {
            workDay.isHoliday -> "Выходной"
            start.isNoTime() && finish.isNoTime() -> "Не указано"
            else -> "$start - $finish"
        }
    }

    override fun createNewHolder(parent: ViewParent) = WorkDayHolder()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as WeekSequenceDayModel

        if (workDay != other.workDay) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + workDay.hashCode()
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
