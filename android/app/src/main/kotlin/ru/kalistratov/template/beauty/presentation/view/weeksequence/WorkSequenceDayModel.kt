package ru.kalistratov.template.beauty.presentation.view.weeksequence

import android.content.Context
import android.view.View
import android.view.ViewParent
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.infrastructure.extensions.isNoTime
import ru.kalistratov.template.beauty.infrastructure.extensions.toClockFormat

class WeekSequenceDayModel(
    private val workday: SequenceDay,
    private val clickListener: (Int) -> Unit = {},
) : EpoxyModelWithHolder<WeekSequenceDayModel.WorkDayHolder>() {

    private data class State(
        val text: String,
        @ColorRes val colorId: Int,
        @DrawableRes val strokeId: Int,
    )

    init {
        id(workday.day.hashCode())
    }

    override fun getDefaultLayout(): Int = R.layout.list_item_sequence_day

    override fun bind(holder: WorkDayHolder) = with(holder) {
        root?.setOnClickListener { clickListener.invoke(workday.day.index) }

        weekDayTextView?.apply {
            text = context?.getString(workday.day.shortTittleResId)
        }

        val context = root?.context ?: return@with
        val state = getState(context)
        val textColor = ContextCompat.getColor(context, state.colorId)
        timeTextView?.text = state.text
        timeTextView?.setTextColor(textColor)
        weekDayTextView?.setTextColor(textColor)
        constraint?.background = ContextCompat.getDrawable(context, state.strokeId)
    }

    private fun getState(context: Context): State {
        val start = workday.startAt
        val finish = workday.finishAt
        return when {
            workday.isHoliday -> State(
                context.getString(R.string.holiday),
                R.color.colorAcceptTransparent50,
                R.drawable.background_stroke_round_transparent,
            )
            start.isNoTime() && finish.isNoTime() -> State(
                context.getString(R.string.not_specified),
                R.color.colorOrange,
                R.drawable.background_stroke_round_orange,
            )
            else -> State(
                "${start.toClockFormat()} - ${finish.toClockFormat()}",
                R.color.light_primary,
                R.drawable.background_stroke_round_accept,
            )
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
        result = 31 * result + workday.isHoliday.hashCode()
        result = 31 * result + workday.startAt.hashCode()
        result = 31 * result + workday.finishAt.hashCode()
        return result
    }

    class WorkDayHolder : EpoxyHolder() {
        var root: View? = null
        var constraint: View? = null
        var timeTextView: TextView? = null
        var weekDayTextView: TextView? = null
        override fun bindView(itemView: View) = with(itemView) {
            root = itemView
            constraint = findViewById(R.id.constraint)
            timeTextView = findViewById(R.id.time_text_view)
            weekDayTextView = findViewById(R.id.week_day_text_view)
        }
    }
}
