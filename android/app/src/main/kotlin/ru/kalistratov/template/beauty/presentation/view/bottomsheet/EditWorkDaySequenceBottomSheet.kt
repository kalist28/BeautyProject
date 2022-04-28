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
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.domain.extension.*
import ru.kalistratov.template.beauty.presentation.extension.find

class EditWorkDaySequenceBottomSheet(
    val workdaySequence: WorkdaySequence
) : BaseBottomSheet() {

    sealed interface ClickIntent {
        data class EditWindows(val workdaySequence: WorkdaySequence) : ClickIntent
    }

    companion object {
        private var onClickAction: ((ClickIntent) -> Unit)? = null
        private var onSavingButtonClickAction: ((WorkdaySequence?) -> Unit)? = null

        fun savingDay() = callbackFlow {
            onSavingButtonClickAction = { it?.let { trySend(it) } }
            awaitClose { onSavingButtonClickAction = null }
        }.conflate()

        fun clicks() = callbackFlow {
            onClickAction = { trySend(it) }
            awaitClose { onClickAction = null }
        }.conflate()
    }

    private var savingButton: Button? = null
    private var editWindowsButton: Button? = null
    private var holidayCheckBox: CheckBox? = null
    private var timeRangeTextView: TextView? = null
    private var timePicker: TimeRangePicker? = null

    private var endTime = TimeRangePicker.Time(0, 0)
    private var startTime = TimeRangePicker.Time(0, 0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_edit_day_sequence, container, false)

    override fun findView() {
        timePicker = find(R.id.time_picker)
        savingButton = find(R.id.saving_button)
        holidayCheckBox = find(R.id.holiday_checkbox)
        editWindowsButton = find(R.id.windows_edit_btn)
        timeRangeTextView = find(R.id.time_range_text_view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTimePickerListener()
        initBottomSheetBehavior()
        initSavingButtonListener()
        initEditWindowsButtonListener()
        updateViewsByWorkDay(workdaySequence)
    }

    private fun getUpdatedWorkDaySequence(): WorkdaySequence {
        val startFormatted = toLocalTime(startTime)
        val endFormatted = toLocalTime(endTime)
        val isHoliday = holidayCheckBox?.isChecked ?: false

        return workdaySequence.copy(
            startAt = startFormatted,
            finishAt = endFormatted,
            isHoliday = isHoliday
        )
    }

    private fun updateViewsByWorkDay(day: WorkdaySequence) {
        val fromMinutes = day.startAt.getTotalMinute()
        val toMinutes = day.finishAt.getTotalMinute()
        timePicker?.apply {
            startTimeMinutes = fromMinutes
            endTimeMinutes = toMinutes
        }
        updateTimeRangeTextView(day)

        holidayCheckBox?.isChecked = day.isHoliday
    }

    private fun updateTimeRangeTextView(day: WorkdaySequence? = null) {
        val from = (day?.startAt ?: toLocalTime(startTime)).toClockFormat()
        val to = (day?.finishAt ?: toLocalTime(endTime)).toClockFormat()
        val text = "$from - $to"
        timeRangeTextView?.text = text
    }

    private fun toLocalTime(time: TimeRangePicker.Time) =
        time.calendar.time.toTime()

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

    private fun initEditWindowsButtonListener() = editWindowsButton
        ?.setOnClickListener {
            dismiss()
            val workdaySequence = workdaySequence
            onClickAction?.invoke(ClickIntent.EditWindows(workdaySequence))
        }

    private fun initSavingButtonListener() = savingButton
        ?.setOnClickListener {
            dismiss()
            onSavingButtonClickAction?.invoke(getUpdatedWorkDaySequence())
        }
}
