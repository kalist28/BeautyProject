package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.ViewParent
import android.widget.EditText
import androidx.annotation.StringRes
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.textfield.TextInputLayout
import com.soywiz.klock.Time
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemTimeRangeVerticalBinding
import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.entity.TimeRange
import ru.kalistratov.template.beauty.infrastructure.extensions.*
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.sequence.SequenceDayWindowModel
import ru.kalistratov.template.beauty.presentation.view.setMargins

class EditSequenceDayWindowsController : EpoxyController() {

    private val saveClicks = mutableSharedFlow<Unit>()
    private val windowClicks = mutableSharedFlow<Id>()
    private val selectChanges = mutableSharedFlow<List<Id>>()
    private val timeClicks = mutableSharedFlow<TimeSourceType>()
    private val stateChanges = mutableSharedFlow<State>()

    val selector = IdSelector()
    var state = State.LIST

    var day: SequenceDay? = null
    var windows: List<SequenceDayWindow> = emptyList()
    var selectedWindow: SequenceDayWindow? = null

    override fun buildModels() {
        val day = day
        val selectedWindow = selectedWindow
        if (state == State.LIST || state == State.SELECTOR) windows.forEachIndexed { index, window ->
            createSequenceDayWindowModel(index, window).addTo(this)
        } else if (day != null && selectedWindow != null) {
            WindowsTimeRangeViewModel(
                day,
                selectedWindow,
                timeClicks::tryEmit,
                { saveClicks.tryEmit(Unit) }
            ).addTo(this)
        }
    }

    fun clicks() = windowClicks.asSharedFlow()
    fun saveClicks() = saveClicks.asSharedFlow()
    fun timeClicks() = timeClicks.asSharedFlow()
    fun stateChanges() = stateChanges.asSharedFlow()
    fun selectChanges() = selectChanges.asSharedFlow()

    fun cancelSelection() {
        changeState()
        requestModelBuild()
    }

    private fun onWindowClick(id: Id) {
        when (selector.isSelector()) {
            true -> with(selector) {
                windowClicked(id)
                selectChanges.tryEmit(getSelected())
                requestModelBuild()
            }
            false -> windowClicks.tryEmit(id)
        }
    }

    private fun onWindowLongClick(id: Id) {
        changeState()
        onWindowClick(id)
    }

    private fun changeState() = stateChanges
        .tryEmit(selector.changeState())

    private fun createSequenceDayWindowModel(
        number: Int,
        window: SequenceDayWindow
    ) = SequenceDayWindowModel(
        number, window,
        ::onWindowClick,
        ::onWindowLongClick,
        selector.isSelector(),
        selector.contains(window.id)
    )
}

class IdSelector {
    private val selectedIds = mutableListOf<Id>()

    var state = State.LIST

    fun getSelected(): List<Id> = selectedIds.toList()

    fun contains(id: Id) = selectedIds.contains(id)

    fun isSelector() = state == State.SELECTOR

    fun windowClicked(id: Id) {
        if (contains(id)) selectedIds.remove(id)
        else selectedIds.add(id)
    }

    fun changeState(): State {
        state = when (state == State.SELECTOR) {
            true -> State.LIST
            false -> State.SELECTOR
        }
        if (!isSelector()) selectedIds.clear()
        return state
    }
}

data class WindowsTimeRangeViewModel(
    private val day: SequenceDay,
    private val selectedWindow: SequenceDayWindow,
    private val timeClickAction: (TimeSourceType) -> Unit,
    private val saveAction: () -> Unit,
    private val marginsBundle: MarginsBundle? = MarginsBundle.baseHorizontal,
) : EpoxyModelWithHolder<WindowsTimeRangeViewModel.Holder>() {

    init {
        id("edit_window")
    }

    private val windows = if (!selectedWindow.id.exist()) day.windows
    else day.windows.toMutableList().apply { removeIf { it.id == selectedWindow.id } }

    private val dayTimeRange = day.let { TimeRange(it.startAt, it.finishAt) }

    private val windowsTimeRanges = this.windows.map { it.startAt.toTimeRange(it.finishAt) }

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        marginsBundle?.let(root::setMargins)

        saveBtn.setOnClickListener { saveAction.invoke() }

        startEditText.initTimeEditFile(selectedWindow.timeSource(TimeSourceType.START_KEY))
        finishEditText.initTimeEditFile(selectedWindow.timeSource(TimeSourceType.FINISH_KEY))

        val context = root.context
        val startTime = selectedWindow.startAt
        val finishTime = selectedWindow.finishAt

        if (checkOutsideError(startTime, finishTime, holder)) {
            saveBtn.isEnabled = false
            return
        }

        updateInsideError(
            finishInputLayout,
            windowsTimeRanges.find(finishTime::insideWithoutEnd)
        )

        updateInsideError(
            startInputLayout,
            windowsTimeRanges.find(startTime::insideWithoutEnd)
        )

        var insideFreeWindows = false
        val freeWindows = calculateFreeWindows()
        val newWindowRange = TimeRange(startTime, finishTime)

        for (freeWindow in freeWindows) {
            if (newWindowRange.insideOf(freeWindow)) {
                insideFreeWindows = true; break
            }
        }

        saveBtn.isEnabled = insideFreeWindows
        finishInputLayout.error = when (!insideFreeWindows) {
            true -> context.getString(R.string.shadow_windows_error)
            false -> null
        }

    }

    private fun EditText.initTimeEditFile(source: TimeSource) {
        setText(source.time.toClockFormat())
        isClickable = true
        inputType = InputType.TYPE_NULL

        setOnClickListener { timeClickAction(source.type) }
        setOnFocusChangeListener { _, focus ->
            if (!focus) return@setOnFocusChangeListener
            timeClickAction(source.type)
            clearFocus()
        }
    }

    private fun checkOutsideError(
        startTime: Time,
        finishTime: Time,
        holder: Holder
    ): Boolean = with(holder.binding) {
        val startInsideWorkTime = startTime in dayTimeRange
        val finishInsideWorkTime = finishTime in dayTimeRange

        if (updateTimeEqualsOrInverseRangeError(startTime, finishTime, holder)) return true

        updateOutsideWorkTimeError(
            startInputLayout,
            when {
                startTime > dayTimeRange.end -> dayTimeRange.end
                startInsideWorkTime -> null
                else -> dayTimeRange.start
            }
        )

        updateOutsideWorkTimeError(
            finishInputLayout,
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
        finishTime: Time,
        holder: Holder
    ): Boolean {
        val startMills = startTime.toMilliseconds()
        val finishMills = finishTime.toMilliseconds()
        val timesEquals = startMills == finishMills
        val inverseRange = startMills > finishMills

        with(holder.binding) {
            val context = root.context
            startInputLayout.error = context.getString(
                when {
                    timesEquals -> R.string.times_equals
                    inverseRange -> R.string.inverse_range
                    else -> R.string.blank_string
                }
            )
            finishInputLayout.error = context.getString(
                when {
                    timesEquals -> R.string.times_equals
                    else -> R.string.blank_string
                }
            )
        }
        return timesEquals or inverseRange
    }

    private fun updateOutsideWorkTimeError(
        view: TextInputLayout?,
        time: Time?
    ) = view?.let {
        view.error = getErrorMessage(
            view.context,
            time,
            R.string.outside_work_time,
            time.toClockFormat()
        )
    }

    private fun updateInsideError(
        view: TextInputLayout?,
        timeRange: TimeRange?
    ) = view?.let {
        view.error = getErrorMessage(
            view.context,
            timeRange,
            R.string.inside_work_window,
            timeRange.toString()
        )
    }

    private fun getErrorMessage(
        context: Context,
        obj: Any?,
        @StringRes errorPattern: Int,
        arg: String
    ) = obj?.let { context.getString(errorPattern, arg) }

    override fun getDefaultLayout() = R.layout.list_item_time_range_vertical

    override fun createNewHolder(parent: ViewParent) = Holder()

    class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTimeRangeVerticalBinding
        override fun bindView(itemView: View) {
            binding = ListItemTimeRangeVerticalBinding.bind(itemView)
        }
    }
}