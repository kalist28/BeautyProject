package ru.kalistratov.template.beauty.presentation.view.workdaywindows

import android.view.View
import android.view.ViewParent
import android.widget.TextView
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.extension.getTotalMinute
import ru.kalistratov.template.beauty.domain.extension.toClockFormat

data class WorkdayWindowModel(
    private val number: Int,
    private val workdayWindow: WorkdayWindow,
    private val clickAction: (Long) -> Unit
) : EpoxyModelWithHolder<WorkdayWindowModel.Holder>() {

    init {
        id(workdayWindow.hashCode())
    }

    override fun getDefaultLayout() = R.layout.item_workday_window

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun bind(holder: Holder) {
        with(holder) {
            val formatStart = workdayWindow.startAt.toClockFormat()
            val formatFinish = workdayWindow.finishAt.toClockFormat()
            val formatTime = "$formatStart - $formatFinish"
            timeTextView?.text = formatTime
            numberTextView?.text = (number + 1).toString()
            minutesTextView?.let {
                val minutes = workdayWindow.run {
                    finishAt.getTotalMinute() - startAt.getTotalMinute()
                }
                it.text = it.context.getString(R.string.total_minutes, minutes)
            }
            root?.setOnClickListener { clickAction.invoke(workdayWindow.id) }
        }
    }

    class Holder : EpoxyHolder() {
        var root: View? = null
        var timeTextView: TextView? = null
        var numberTextView: TextView? = null
        var minutesTextView: TextView? = null

        override fun bindView(itemView: View) {
            root = itemView
            timeTextView = itemView.findViewById(R.id.time_text_view)
            numberTextView = itemView.findViewById(R.id.week_day_text_view)
            minutesTextView = itemView.findViewById(R.id.minutes_text_view)
        }
    }
}
