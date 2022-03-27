package ru.kalistratov.template.beauty.presentation.view.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import nl.joery.timerangepicker.TimeRangePicker
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkDaySequence
import ru.kalistratov.template.beauty.domain.extension.*
import ru.kalistratov.template.beauty.presentation.extension.find

class EditWorkDaySequenceBottomSheet : BaseBottomSheet() {

    companion object {
        private var onSavingButtonClickAction: ((WorkDaySequence?) -> Unit)? = null

        fun savingDay() = callbackFlow {
            onSavingButtonClickAction = { it?.let { trySend(it) } }
            awaitClose { onSavingButtonClickAction = null }
        }.conflate()
    }

    private var savingButton: Button? = null
    private var holidayCheckBox: CheckBox? = null
    private var timeRangeTextView: TextView? = null
    private var timePicker: TimeRangePicker? = null

    private var endTime = TimeRangePicker.Time(0, 0)
    private var startTime = TimeRangePicker.Time(0, 0)

    var workDaySequence: WorkDaySequence? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_edit_day_sequence, container, false)

    override fun findView() {
        timePicker = find(R.id.time_picker)
        savingButton = find(R.id.saving_button)
        holidayCheckBox = find(R.id.holiday_checkbox)
        timeRangeTextView = find(R.id.time_range_text_view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTimePickerListener()
        initBottomSheetBehavior()
        initSavingButtonListener()
        workDaySequence?.let { updateViewsByWorkDay(it) }
    }

    private fun getUpdatedWorkDaySequence(): WorkDaySequence? {
        val startFormatted = getFormatTime(startTime)
        val endFormatted = getFormatTime(endTime)
        val isHoliday = holidayCheckBox?.isChecked ?: false

        return workDaySequence?.copy(
            startAt = startFormatted,
            finishAt = endFormatted,
            isHoliday = isHoliday
        )
    }

    private fun updateViewsByWorkDay(day: WorkDaySequence) {
        val fromMinutes = day.startAt.toCalendar().getTotalMinute()
        val toMinutes = day.finishAt.toCalendar().getTotalMinute()
        timePicker?.apply {
            startTimeMinutes = fromMinutes
            endTimeMinutes = toMinutes
        }
        updateTimeRangeTextView(day)

        holidayCheckBox?.isChecked = day.isHoliday
    }

    private fun updateTimeRangeTextView(day: WorkDaySequence? = null) {
        val from = day?.startAt ?: getFormatTime(startTime)
        val to = day?.finishAt ?: getFormatTime(endTime)
        val text = "$from - $to"
        timeRangeTextView?.text = text
    }

    private fun getFormatTime(time: TimeRangePicker.Time) =
        time.calendar.time.toClockDate()

    @SuppressLint("ClickableViewAccessibility")
    private fun initBottomSheetBehavior() = (dialog as BottomSheetDialog).behavior
        .apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            timePicker?.setOnTouchListener { timePicker, motionEvent ->
                this.isDraggable = when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE -> false
                    else -> true
                }
                timePicker.performClick()
                false
            }
        }

    private fun initTimePickerListener() = timePicker?.setOnTimeChangeListener(
        object : TimeRangePicker.OnTimeChangeListener {
            override fun onDurationChange(duration: TimeRangePicker.TimeDuration) =
                this@EditWorkDaySequenceBottomSheet.let {
                    it.endTime = duration.end
                    it.startTime = duration.start
                    updateTimeRangeTextView()
                }

            override fun onEndTimeChange(endTime: TimeRangePicker.Time) = Unit
            override fun onStartTimeChange(startTime: TimeRangePicker.Time) = Unit
        }
    )

    private fun initSavingButtonListener() = savingButton
        ?.setOnClickListener {
            onSavingButtonClickAction?.invoke(getUpdatedWorkDaySequence())
            dismiss()
        }
}
