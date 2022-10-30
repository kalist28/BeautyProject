package ru.kalistratov.template.beauty.presentation.view.bottomsheet

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import com.soywiz.klock.DateTime
import com.soywiz.klock.Time
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.presentation.extension.find

class TimePickerSpinnerBottomSheet(
    private val resultTag: String = TIME_TAG
) : BaseBottomSheet() {

    data class TimePickerResult(
        val tag: String,
        val time: Time
    )

    companion object {
        const val TIME_TAG = "time"
        const val TO_TIME_TAG = "to_time"
        const val FROM_TIME_TAG = "from_time"

        private var onSavingButtonClickAction: ((TimePickerResult) -> Unit)? = null

        fun savingTime() = callbackFlow {
            onSavingButtonClickAction = { trySend(it) }
            awaitClose { onSavingButtonClickAction = null }
        }.conflate()
    }

    private var saveButton: Button? = null
    private var timePicker: TimePicker? = null

    override fun findView() {
        saveButton = find(R.id.save_btn)
        timePicker = find(R.id.time_picker)
    }

    override fun getSheetTag() = "TimePickerSpinnerBottomSheet"
    override fun isFullscreen() = true

    private var hour = 0
    private var minute = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.bottom_sheet_time_picker_spinner,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timePicker?.also {
            it.setIs24HourView(true)
            it.setOnTimeChangedListener { _, hour, minute -> setTime(hour, minute) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                setTime(it.hour, it.minute)
            else setTime(it.currentHour, it.currentMinute)
        }

        saveButton?.setOnClickListener {
            val result = TimePickerResult(
                resultTag,
                Time(hour, minute)
            )
            onSavingButtonClickAction?.invoke(result)
            dismiss()
        }
    }

    private fun setTime(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
    }
}
