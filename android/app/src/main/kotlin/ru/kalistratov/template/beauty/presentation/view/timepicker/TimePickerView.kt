package ru.kalistratov.template.beauty.presentation.view.timepicker

import android.content.Context
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import com.soywiz.klock.DateTime
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class TimePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val hoursTextView by lazy { findViewById<EditText>(R.id.hours) }
    private val minutesTextView by lazy { findViewById<EditText>(R.id.minutes) }

    init {
        inflate(context, R.layout.view_time_picker, this)
        hoursTextView.addTextChangedListener(RangeTextProcessor(IntRange(0, 24), hoursTextView))
        minutesTextView.addTextChangedListener(RangeTextProcessor(IntRange(0, 59), minutesTextView))

        val timeNow = DateTime.now().local
        hoursTextView.setText(timeNow.hours.toString())
        minutesTextView.setText(timeNow.minutes.toString())
    }

    private class RangeTextProcessor(
        val range: IntRange,
        val parent: EditText
    ) : TextWatcher {
        var beforeText: String = ""
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeText = s?.toString() ?: ""
        }
        override fun afterTextChanged(s: Editable?) {
            s?.apply {
                if (s.isBlank() || beforeText == this.toString()) return
                val input = s.toString().toInt()
                if (range.contains(input)) {
                    if (s.length == 1) {
                        parent.setText("0$s")
                    }
                    if (s.length > 2) {
                        parent.setText(s.subSequence(s.length - 2, s.length))
                    }
                } else {
                    parent.setText(
                        when {
                            input > range.last -> range.last
                            input < range.first -> range.first
                            else -> input
                        }.toString()
                    )
                }
                parent.setSelection(parent.text.length)
            }
        }
    }
}
