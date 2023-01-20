package ru.kalistratov.template.beauty.presentation.view.epoxy.sequence

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
import ru.kalistratov.template.beauty.databinding.ListItemSequenceDayBinding
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.exist
import ru.kalistratov.template.beauty.infrastructure.extensions.isNoTime
import ru.kalistratov.template.beauty.infrastructure.extensions.toClockFormat
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class WeekSequenceDayModel(
    private val day: SequenceDay,
    private val clickListener: (Int) -> Unit = {},
) : EpoxyModelWithHolder<WeekSequenceDayModel.WorkDayHolder>() {

    private data class State(
        val text: String,
        @ColorRes val colorId: Int
    )

    init {
        id(day.day.hashCode())
    }

    override fun bind(holder: WorkDayHolder) = with(holder.binding) {
        root.setOnClickListener { clickListener.invoke(day.day.index) }

        weekDayTextView.apply { text = context?.getString(day.day.tittleResId) }

        val context = root.context ?: return@with
        val state = getState(context)
        timeTextView.text = state.text
        indicator.backgroundTintList = context.getColorStateList(state.colorId)
    }

    private fun getState(context: Context): State {
        val start = day.startAt
        val finish = day.finishAt
        return when {
            start.isNoTime() && finish.isNoTime() && !day.id.exist() -> State(
                context.getString(R.string.not_specified),
                R.color.colorOrange,
            )
            day.isHoliday -> State(
                context.getString(R.string.holiday),
                R.color.colorAcceptTransparent50,
            )
            else -> State(
                "${start.toClockFormat()} - ${finish.toClockFormat()}",
                R.color.colorGreen,
            )
        }
    }

    override fun createNewHolder(parent: ViewParent) = WorkDayHolder()

    override fun getDefaultLayout(): Int = R.layout.list_item_sequence_day

    class WorkDayHolder : EpoxyHolder() {
        lateinit var binding: ListItemSequenceDayBinding
        override fun bindView(itemView: View) {
            binding = ListItemSequenceDayBinding.bind(itemView).apply {
                root.setMargins(
                    MarginsBundle.baseHorizontal.copy(
                        topMarginDp = 4,
                        bottomMarginDp = 4
                    )
                )
            }
        }
    }
}
