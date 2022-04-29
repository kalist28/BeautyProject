package ru.kalistratov.template.beauty.presentation.view.workdaywindows

import android.view.View
import android.view.ViewParent
import android.widget.TextView
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.extension.toClockFormat

class WorkdayWindowModel(
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
            time?.text = formatTime
            root?.setOnClickListener { clickAction.invoke(workdayWindow.id) }
        }
    }

    class Holder : EpoxyHolder() {
        var root: View? = null
        var from: TextView? = null
        var time: TextView? = null

        // TODO naming
        override fun bindView(itemView: View) {
            root = itemView
            time = itemView.findViewById(R.id.time_text_view)
        }
    }
}
