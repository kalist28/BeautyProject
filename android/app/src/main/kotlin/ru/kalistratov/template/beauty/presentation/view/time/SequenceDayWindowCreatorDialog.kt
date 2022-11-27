package ru.kalistratov.template.beauty.presentation.view.time

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.soywiz.klock.Time
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import ru.kalistratov.template.beauty.infrastructure.extensions.*

class SequenceDayWindowCreatorDialog(
    private val sequenceDayWindow: SequenceDayWindow?,
    windows: List<SequenceDayWindow>,
    private val dayTimeRange: TimeRange,
    context: Context,
) : Dialog(context, R.style.BaseDialog) {

    sealed interface Callback {
        data class Add(val window: SequenceDayWindow) : Callback
        data class Update(val window: SequenceDayWindow) : Callback
    }

    companion object {
        private var callbackListener: (Callback) -> Unit = {}

        val saves = callbackFlow {
            callbackListener = { trySend(it) }
            awaitClose { callbackListener = {} }
        }.conflate()
    }

    private val windows = when (sequenceDayWindow == null) {
        true -> windows
        false -> windows.toMutableList()
            .also { list -> list.removeIf { it.id == sequenceDayWindow.id } }
    }

    private val timeRanges = this.windows.map { it.startAt.toTimeRange(it.finishAt) }

    private val startError: TextView by lazy { findViewById(R.id.start_error_message) }
    private val finishError: TextView by lazy { findViewById(R.id.finish_error_message) }

    private val startEditTime by lazy {
        findViewById<EditTimeView>(R.id.start_edit_time)
            .apply { onChangeListener = { setAllowSave() } }
    }

    private val finishEditTime by lazy {
        findViewById<EditTimeView>(R.id.finish_edit_time)
            .apply { onChangeListener = { setAllowSave() } }
    }

    private val saveButton by lazy {
        findViewById<Button>(R.id.save_btn).apply {
            text = context.getString(
                when (sequenceDayWindow == null) {
                    true -> R.string.add
                    false -> R.string.edit
                }
            )
            setOnClickListener {
                callbackListener.invoke(getResultCallback())
                dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_sequence_day_window)
        findViews()
        window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                context,
                R.drawable.dialog_background
            )
        )

        setAllowSave()
        initTimeRange()
    }

    private fun initTimeRange() {
        when (sequenceDayWindow != null) {
            true -> sequenceDayWindow.let { it.startAt to it.finishAt }
            false -> windows.lastOrNull()
                ?.let { it.finishAt to it.finishAt.plus(hour = 1) }
                ?: dayTimeRange.start.let { it to it.plus(hour = 1) }
        }.let {
            startEditTime.time = it.first
            finishEditTime.time = it.second
        }
    }

    private fun findViews() {
        findViewById<TextView>(R.id.topic_text_view).text = context.getString(
            when (sequenceDayWindow == null) {
                true -> R.string.create_work_window
                false -> R.string.edit_work_window
            }
        )

        findViewById<Button>(R.id.close_btn)?.apply {
            setOnClickListener { dismiss() }
        }
    }

    private fun getResultCallback(): Callback =
        when (sequenceDayWindow == null) {
            true -> Callback.Add(createWindow())
            else -> Callback.Update(updateWindow(sequenceDayWindow))
        }

    private fun createWindow() = SequenceDayWindow(
        startAt = startEditTime?.time ?: noTime,
        finishAt = finishEditTime?.time ?: noTime
    )

    private fun updateWindow(day: SequenceDayWindow) = day.copy(
        startAt = startEditTime?.time ?: day.startAt,
        finishAt = finishEditTime?.time ?: day.finishAt
    )

    private fun setAllowSave() {
        val startTime = startEditTime?.time ?: return
        val finishTime = finishEditTime?.time ?: return

        if (checkOutsideError(startTime, finishTime)) {
            saveButton?.isEnabled = false
            return
        }

        timeRanges.find { finishTime.insideWithoutCorners(it) }.let {
            updateInsideError(finishError, it)
            finishEditTime?.error = (it == null).not()
        }

        timeRanges.find { startTime.insideWithoutCorners(it) }.let {
            updateInsideError(startError, it)
            startEditTime?.error = (it == null).not()
        }

        var insideFreeWindows = false
        val freeWindows = calculateFreeWindows()
        val newWindowRange = TimeRange(startTime, finishTime)

        for (freeWindow in freeWindows) {
            if (newWindowRange.insideOf(freeWindow)) {
                insideFreeWindows = true
                break
            }
        }
        saveButton?.isEnabled = insideFreeWindows

        finishError.text = when (!insideFreeWindows) {
            true -> context.getString(R.string.shadow_windows_error)
            false -> null
        }
    }

    private fun checkOutsideError(startTime: Time, finishTime: Time): Boolean {
        val startInsideWorkTime = startTime in dayTimeRange
        val finishInsideWorkTime = finishTime in dayTimeRange

        updateTimeEqualsOrInverseRangeError(startTime, finishTime)
            .let { if (it) return it }

        updateOutsideWorkTimeError(
            startError,
            when {
                startTime > dayTimeRange.end -> dayTimeRange.end
                startInsideWorkTime -> null
                else -> dayTimeRange.start
            }
        )

        updateOutsideWorkTimeError(
            finishError,
            when {
                finishTime < dayTimeRange.start -> dayTimeRange.start
                finishInsideWorkTime -> null
                else -> dayTimeRange.end
            }
        )

        return startInsideWorkTime.not() or finishInsideWorkTime.not()
    }

    private fun calculateFreeWindows(): List<TimeRange> = mutableListOf<TimeRange>()
        .apply {
            if (windows.isEmpty()) add(dayTimeRange)
            else {
                add(getFirstFreeWindow())
                for (index in 0 until windows.size - 1) {
                    val first = windows[index]
                    val second = windows[index + 1]
                    add(TimeRange(first.finishAt, second.startAt))
                }
                add(getLastFreeWindow())
            }
        }

    private fun getFirstFreeWindow() = TimeRange(
        dayTimeRange.start,
        windows.first().startAt
    )

    private fun getLastFreeWindow() = TimeRange(
        windows.last().finishAt,
        dayTimeRange.end
    )

    private fun updateTimeEqualsOrInverseRangeError(
        startTime: Time,
        finishTime: Time
    ): Boolean {
        val startMills = startTime.toMilliseconds()
        val finishMills = finishTime.toMilliseconds()
        val timesEquals = startMills == finishMills
        val inverseRange = startMills > finishMills


        startError.text = null
        finishError.text = context.getString(
            when {
                timesEquals -> R.string.times_equals
                inverseRange -> R.string.inverse_range
                else -> R.string.blank_string
            }
        )

        return timesEquals or inverseRange
    }

    private fun updateOutsideWorkTimeError(
        view: TextView?,
        time: Time?
    ) = view?.let {
        showErrorMessage(
            textView = it,
            message = getErrorMessage(
                time,
                R.string.outside_work_time,
                time.toClockFormat()
            )
        )
    }

    private fun updateInsideError(
        view: TextView?,
        timeRange: TimeRange?
    ) = view?.let {
        showErrorMessage(
            textView = it,
            message = getErrorMessage(
                timeRange,
                R.string.inside_work_window,
                timeRange.toString()
            )
        )
    }

    private fun showErrorMessage(
        textView: TextView,
        message: String
    ) {
        textView.text = message
    }

    private fun getErrorMessage(
        obj: Any?,
        @StringRes errorPattern: Int,
        arg: String
    ) = obj?.let { context.getString(errorPattern, arg) } ?: ""
}
