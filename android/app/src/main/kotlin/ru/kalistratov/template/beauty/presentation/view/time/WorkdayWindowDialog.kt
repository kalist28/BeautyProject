package ru.kalistratov.template.beauty.presentation.view.time

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import com.soywiz.klock.Time
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.extension.*
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange

class WorkdayWindowDialog(
    private val workdayWindow: WorkdayWindow?,
    windows: List<WorkdayWindow>,
    private val workdayTimeRange: TimeRange,
    context: Context,
) : Dialog(context) {

    sealed interface Callback {
        data class Add(val window: WorkdayWindow) : Callback
        data class Update(val window: WorkdayWindow) : Callback
    }

    companion object {
        private var callbackListener: (Callback) -> Unit = {}

        val saves = callbackFlow {
            callbackListener = { trySend(it) }
            awaitClose { callbackListener = {} }
        }.conflate()
    }

    private val windows = when (workdayWindow == null) {
        true -> windows
        false -> windows.toMutableList()
            .also { list -> list.removeIf { it.id == workdayWindow.id } }
    }
    private val timeRanges = this.windows.map { it.startAt.toTimeRange(it.finishAt) }

    private var saveButton: Button? = null
    private var closeButton: ImageView? = null
    private var startError: TextView? = null
    private var finishError: TextView? = null
    private var startEditTime: EditTimeView? = null
    private var finishEditTime: EditTimeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time_range)
        findViews()
        window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                context,
                R.drawable.dialog_background
            )
        )

        workdayWindow?.let {
            startEditTime?.time = it.startAt
            finishEditTime?.time = it.finishAt
        }

        setAllowSave()
    }

    private fun findViews() {
        findViewById<TextView>(R.id.topic_text_view).text = context.getString(
            when (workdayWindow == null) {
                true -> R.string.create_work_window
                false -> R.string.edit_work_window
            }
        )
        startError = findViewById(R.id.start_error_message)
        finishError = findViewById(R.id.finish_error_message)

        startEditTime = findViewById<EditTimeView>(R.id.start_edit_time)
            ?.apply { onChangeListener = { setAllowSave() } }

        finishEditTime = findViewById<EditTimeView>(R.id.finish_edit_time)
            ?.apply { onChangeListener = { setAllowSave() } }

        saveButton = findViewById<Button>(R.id.saving_button)
            ?.apply {
                text = context.getString(
                    when (workdayWindow == null) {
                        true -> R.string.add
                        false -> R.string.edit
                    }
                )
                setOnClickListener {
                    callbackListener.invoke(getResultCallback())
                    dismiss()
                }
            }

        closeButton = findViewById<ImageView>(R.id.close_button)
            ?.apply {
                setOnClickListener {
                    dismiss()
                }
            }
    }

    private fun getResultCallback(): Callback =
        when (workdayWindow == null) {
            true -> Callback.Add(createWorkdayWindow())
            else -> Callback.Update(workdayWindow)
        }

    private fun createWorkdayWindow() = WorkdayWindow(
        startAt = startEditTime?.time ?: noTime,
        finishAt = finishEditTime?.time ?: noTime
    )

    private fun setAllowSave() {
        val startTime = startEditTime?.time ?: return
        val finishTime = finishEditTime?.time ?: return

        if (checkOutsideError(startTime, finishTime)) {
            saveButton?.isEnabled = false
            return
        }

        val newWindowRange = TimeRange(startTime, finishTime)

        timeRanges.find { finishTime.insideWithoutCorners(it) }.let {
            updateInsideError(finishError, it)
            finishEditTime?.updateBackground((it == null).not())
        }

        timeRanges.find { startTime.insideWithoutCorners(it) }.let {
            updateInsideError(startError, it)
            startEditTime?.updateBackground((it == null).not())
        }

        var contains = false

        val freeWindows = calculateFreeWindows()
        for (freeWindow in freeWindows) {
            if (newWindowRange.insideOf(freeWindow)) {
                contains = true
                break
            }
        }
        saveButton?.isEnabled = contains
    }

    private fun checkOutsideError(startTime: Time, finishTime: Time): Boolean {
        val startInsideWorkTime = startTime in workdayTimeRange
        val finishInsideWorkTime = finishTime in workdayTimeRange

        updateTimeEqualsOrInverseRangeError(startTime, finishTime)
            .let { if (it) return it }

        updateOutsideWorkTimeError(
            startError,
            when {
                startTime > workdayTimeRange.end -> workdayTimeRange.end
                startInsideWorkTime -> null
                else -> workdayTimeRange.start
            }
        )

        updateOutsideWorkTimeError(
            finishError,
            when {
                finishTime < workdayTimeRange.start -> workdayTimeRange.start
                finishInsideWorkTime -> null
                else -> workdayTimeRange.end
            }
        )

        return startInsideWorkTime.not() or finishInsideWorkTime.not()
    }

    private fun calculateFreeWindows(): List<TimeRange> = mutableListOf<TimeRange>()
        .apply {
            if (windows.isEmpty()) add(workdayTimeRange)
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
        workdayTimeRange.start,
        windows.first().startAt
    )

    private fun getLastFreeWindow() = TimeRange(
        windows.last().finishAt,
        workdayTimeRange.end
    )

    private fun updateTimeEqualsOrInverseRangeError(
        startTime: Time,
        finishTime: Time
    ): Boolean {
        val startMills = startTime.toMilliseconds()
        val finishMills = finishTime.toMilliseconds()
        val timesEquals = startMills == finishMills
        val inverseRange = startMills > finishMills


        startError?.text = null
        finishError?.text = context.getString(
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
