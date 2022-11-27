package ru.kalistratov.template.beauty.presentation.view.workdaywindows

import android.view.View
import android.view.ViewParent
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.radiobutton.MaterialRadioButton
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.infrastructure.extensions.getTotalMinute
import ru.kalistratov.template.beauty.infrastructure.extensions.toClockFormat

//TODO Переписатькогда нить это говно с выбором, щас жалко удалять тк потратил 2 дня на это.
data class SequenceDayWindowModel(
    private val number: Int,
    private val sequenceDayWindow: SequenceDayWindow,
    private val clickAction: (Id) -> Unit,
    private val longAction: (Id) -> Unit,
    private val selector: Boolean,
    private val selected: Boolean,
) : EpoxyModelWithHolder<SequenceDayWindowModel.Holder>() {

    init {
        id(sequenceDayWindow.hashCode())
    }

    private val motionListener = object : MotionLayout.TransitionListener {
        var started = false

        override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
            started = true
        }

        override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            started = false
        }

        override fun onTransitionChange(a: MotionLayout?, b: Int, c: Int, d: Float) = Unit
        override fun onTransitionTrigger(a: MotionLayout?, b: Int, c: Boolean, d: Float) = Unit
    }

    override fun getDefaultLayout() = R.layout.list_item_sequence_day_window

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun bind(holder: Holder) {
        with(holder) {
            val formatStart = sequenceDayWindow.startAt.toClockFormat()
            val formatFinish = sequenceDayWindow.finishAt.toClockFormat()
            val formatTime = "$formatStart - $formatFinish"
            timeTextView?.text = formatTime
            numberTextView?.text = (number + 1).toString()
            minutesTextView?.let {
                val minutes = sequenceDayWindow.run {
                    finishAt.getTotalMinute() - startAt.getTotalMinute()
                }
                it.text = it.context.getString(R.string.total_minutes, minutes)
            }
            val id = sequenceDayWindow.id
            root?.setOnClickListener { clickAction.invoke(id) }
            root?.setOnLongClickListener {
                longAction.invoke(id)
                true
            }
            radioButton?.isChecked = selected
            animSelector(holder)
        }
    }

    override fun preBind(view: Holder, model: EpoxyModel<*>?) {
        view.motionLayout?.setTransitionListener(motionListener)
        quickSelectorAnim(view)
    }

    override fun unbind(holder: Holder) =
        quickSelectorAnim(holder)

    override fun onVisibilityStateChanged(visibilityState: Int, holder: Holder) = super
        .onVisibilityStateChanged(visibilityState, holder)
        .also { quickSelectorAnim(holder) }

    private fun animSelector(holder: Holder, duration: Int = 500) = holder.motionLayout
        ?.let {
            if (motionListener.started) return@let
            it.setTransitionDuration(duration)
            if (selector) it.transitionToEnd()
            else it.transitionToStart()
        }

    private fun quickSelectorAnim(holder: Holder) {
        holder.motionLayout?.let {
            it.setTransitionDuration(0)
            if (selector) it.transitionToEnd()
            else it.transitionToStart()
        }
    }


    class Holder() : EpoxyHolder() {
        var root: View? = null
        var radioButton: MaterialRadioButton? = null
        var motionLayout: MotionLayout? = null
        var timeTextView: TextView? = null
        var numberTextView: TextView? = null
        var minutesTextView: TextView? = null

        override fun bindView(itemView: View) {
            root = itemView
            radioButton = itemView.findViewById(R.id.radio_btn)
            motionLayout = itemView.findViewById(R.id.motion_layout)
            timeTextView = itemView.findViewById(R.id.time_text_view)
            numberTextView = itemView.findViewById(R.id.week_day_text_view)
            minutesTextView = itemView.findViewById(R.id.minutes_text_view)
        }
    }
}
