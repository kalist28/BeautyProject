package ru.kalistratov.template.beauty.presentation.view.time

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View.OnKeyListener
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.soywiz.klock.Time
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.extensions.timeNow
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class EditTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private val hoursRange = IntRange(0, 23)
        private val minutesRange = IntRange(0, 59)
    }

    private val updatesFlow = MutableSharedFlow<Time>(0, 1)

    private val hoursTextView by lazy { findViewById<EditText>(R.id.hours) }
    private val minutesTextView by lazy { findViewById<EditText>(R.id.minutes) }

    var onChangeListener: (Time) -> Unit = {}

    private val onClearListener = OnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
            if (v is EditText) v.clearFocus()
        }
        false
    }

    var editable: Boolean = false
        set(editable) {
            field = editable
            hoursTextView.isEnabled = editable
            minutesTextView.isEnabled = editable
            setBackgroundOrDefault(
                !editable,
                R.drawable.background_time_picker_start_uneditable,
                R.drawable.background_time_picker_end_uneditable
            )
        }

    var error: Boolean = false
        set(error) {
            field = error
            if (!editable) return
            setBackgroundOrDefault(
                error,
                R.drawable.background_time_picker_start_error,
                R.drawable.background_time_picker_end_error
            )
        }

    var time: Time
        set(value) {
            loge("set ${value}")
            hoursTextView.setText(value.hour.toString())
            minutesTextView.setText(value.minute.toString())
        }
        get() = Time(
            hoursTextView.text.toString().toInt(),
            minutesTextView.text.toString().toInt()
        )

    init {
        inflate(context, R.layout.view_edit_time, this)

        hoursTextView.apply {
            setOnKeyListener(onClearListener)
            addTextChangedListener(RangeTextProcessor(hoursRange, this))
        }

        minutesTextView.apply {
            setOnKeyListener(onClearListener)
            addTextChangedListener(RangeTextProcessor(minutesRange, minutesTextView))
        }
        time = timeNow()
    }

    fun timeUpdates() = updatesFlow.asSharedFlow()

    private fun setBackgroundOrDefault(
        error: Boolean,
        @DrawableRes start: Int,
        @DrawableRes end: Int,
    ) {
        hoursTextView.background = AppCompatResources.getDrawable(
            context,
            if (error) start
            else R.drawable.background_time_picker_start
        )
        minutesTextView.background = AppCompatResources.getDrawable(
            context,
            if (error) end
            else R.drawable.background_time_picker_end
        )
    }

    private inner class RangeTextProcessor(
        val range: IntRange,
        val view: EditText,
        private var beforeText: String = ""
    ) : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeText = s?.toString() ?: ""
        }

        override fun afterTextChanged(s: Editable?) {
            s?.apply {
                if (s.isBlank() || beforeText == this.toString()) return
                val input = s.toString().toInt()
                view.setText(
                    if (range.contains(input)) s.length.let { len ->
                        when {
                            len == 1 -> "0$s"
                            len > 2 -> s.subSequence(len - 2, len)
                            else -> s
                        }
                    }
                    else when {
                        input > range.last -> range.last
                        input < range.first -> range.first
                        else -> input
                    }.toString()
                )
                view.setSelection(view.text.length)
                onChangeListener(time)
            }
        }
    }
}
