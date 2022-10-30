package ru.kalistratov.template.beauty.presentation.view.weeksequence

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.extension.noTime
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.BaseBottomSheet
import ru.kalistratov.template.beauty.presentation.view.time.EditTimeView


class EditSequenceDayBottomSheet : BaseBottomSheet {

    sealed interface ClickIntent {
        data class EditWindows(val day: SequenceDay) : ClickIntent
        data class SaveSequenceDay(val day: SequenceDay) : ClickIntent
    }

    companion object {
        const val TAG = "EditWorkDaySequenceBottomSheet"
        const val SAVED_BUNDLE_TAG = "DAY"

        private val mutableClicksFlow = mutableSharedFlow<ClickIntent>()
        val clicks: Flow<ClickIntent> = mutableClicksFlow.asSharedFlow()
    }

    constructor()

    constructor(day: SequenceDay) {
        sequenceDay = day
    }

    private var sequenceDay: SequenceDay = SequenceDay.emptyDay

    private var savingButton: Button? = null
    private var editWindowsButton: Button? = null
    private var holidaySwitch: SwitchCompat? = null
    private var startEditTime: EditTimeView? = null
    private var endEditTime: EditTimeView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.bottom_sheet_edit_day_sequence,
        container,
        false
    )

    override fun findView() {
        endEditTime = find(R.id.end_edit_time)
        startEditTime = find(R.id.start_edit_time)
        holidaySwitch = find(R.id.holiday_switch)
        savingButton = find(R.id.saving_button)
        editWindowsButton = find(R.id.windows_edit_btn)
    }

    override fun getSheetTag() = TAG

    override fun isFullscreen() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getString(SAVED_BUNDLE_TAG)
            ?.let { sequenceDay = jsonParser.decodeFromString(it) }
    }

    override fun onStart() {
        super.onStart()
        find<TextView>(R.id.topic_text_view)
            .setText(sequenceDay.day.tittleResId)

        if (sequenceDay.id.isBlank())
            editWindowsButton?.isVisible = false

        initBottomSheetBehavior()
        initSavingButtonListener()
        initHolidaySwitchListener()
        initEditWindowsButtonListener()
        updateViewsByWorkDay(sequenceDay)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            SAVED_BUNDLE_TAG,
            jsonParser.encodeToString(
                getUpdatedWorkDaySequence()
            )
        )
    }

    private fun getUpdatedWorkDaySequence(): SequenceDay {
        val startFormatted = startEditTime?.time ?: noTime
        val endFormatted = endEditTime?.time ?: noTime
        val isHoliday = holidaySwitch?.isChecked ?: false

        return sequenceDay.copy(
            startAt = startFormatted,
            finishAt = endFormatted,
            isHoliday = isHoliday
        )
    }

    private fun updateViewsByWorkDay(day: SequenceDay) {
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
        ?.setOnClickListener { emitIntent(ClickIntent.EditWindows(sequenceDay)) }

    private fun initSavingButtonListener() = savingButton
        ?.setOnClickListener {
            emitIntent(
                ClickIntent.SaveSequenceDay(
                    getUpdatedWorkDaySequence()
                )
            )
        }

    private fun emitIntent(intent: ClickIntent, needDismiss: Boolean = true) {
        if (needDismiss) dismiss()
        mutableClicksFlow.tryEmit(intent)
    }

    private fun initHolidaySwitchListener() = holidaySwitch
        ?.setOnCheckedChangeListener { _, checked ->
            val editableEditTime = checked.not()
            endEditTime?.editable = editableEditTime
            startEditTime?.editable = editableEditTime
        }
}
