package ru.kalistratov.template.beauty.presentation.view.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.domain.extension.noTime
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.view.time.EditTimeView

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
    private var holidaySwitch: SwitchCompat? = null
    private var startEditTime: EditTimeView? = null
    private var endEditTime: EditTimeView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_edit_day_sequence, container, false)

    override fun findView() {
        endEditTime = find(R.id.end_edit_time)
        startEditTime = find(R.id.start_edit_time)
        holidaySwitch = find(R.id.holiday_switch)
        savingButton = find(R.id.saving_button)
        editWindowsButton = find(R.id.windows_edit_btn)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        find<TextView>(R.id.topic_text_view).setText(workdaySequence.day.tittleResId)

        initBottomSheetBehavior()
        initSavingButtonListener()
        initEditWindowsButtonListener()
        updateViewsByWorkDay(workdaySequence)
    }

    private fun getUpdatedWorkDaySequence(): WorkdaySequence {
        val startFormatted = startEditTime?.time ?: noTime
        val endFormatted = endEditTime?.time ?: noTime
        val isHoliday = holidaySwitch?.isChecked ?: false

        return workdaySequence.copy(
            startAt = startFormatted,
            finishAt = endFormatted,
            isHoliday = isHoliday
        )
    }

    private fun updateViewsByWorkDay(day: WorkdaySequence) {
        startEditTime?.time = day.startAt
        endEditTime?.time = day.finishAt

        holidaySwitch?.isChecked = day.isHoliday
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initBottomSheetBehavior() = (dialog as BottomSheetDialog).behavior
        .apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            startEditTime?.setOnTouchListener { timePicker, motionEvent ->
                this.isDraggable = when (motionEvent.action) {
                    MotionEvent.ACTION_MOVE -> false
                    else -> true
                }
                timePicker.performClick()
                false
            }
        }

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
