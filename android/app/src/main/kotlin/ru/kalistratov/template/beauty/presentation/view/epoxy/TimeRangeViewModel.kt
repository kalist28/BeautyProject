package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.text.InputType
import android.view.View
import android.view.ViewParent
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemTimeRangeBinding
import ru.kalistratov.template.beauty.domain.entity.TimeSource
import ru.kalistratov.template.beauty.domain.entity.TimeSourceType
import ru.kalistratov.template.beauty.infrastructure.extensions.toClockFormat
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class TimeRangeViewModel(
    private val id: String,
    private val startTimeSource: TimeSource,
    private val finishTimeSource: TimeSource,
    private val clickAction: (TimeSourceType) -> Unit,
    private val marginsBundle: MarginsBundle? = null,
    private val errorMessageChecker: () -> String? = { null },
    @StringRes private val startHintId: Int? = null,
    @StringRes private val finishHintId: Int? = null,
) : EpoxyModelWithHolder<TimeRangeViewModel.Holder>() {

    init {
        id("time_range${id}")
    }

    override fun getDefaultLayout() = R.layout.list_item_time_range

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        marginsBundle?.let(root::setMargins)
        setError(holder.binding.error)
        startEditText.initTimeEditFile(startTimeSource)
        finishEditText.initTimeEditFile(finishTimeSource)

        startHintId?.let(startInputLayout::setHint)
        finishHintId?.let(finishInputLayout::setHint)
    }

    private fun EditText.initTimeEditFile(source: TimeSource) {
        setText(source.time.toClockFormat())
        isClickable = true
        inputType = InputType.TYPE_NULL

        setOnClickListener { clickAction(source.type) }
        setOnFocusChangeListener { _, focus ->
            if (!focus) return@setOnFocusChangeListener
            clickAction(source.type)
            clearFocus()
        }
    }

    private fun setError(view: TextView) {
        val errorMessage = errorMessageChecker.invoke()
        view.apply {
            isVisible = errorMessage != null
            errorMessage?.let(::setText)
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTimeRangeBinding
        override fun bindView(itemView: View) {
            binding = ListItemTimeRangeBinding.bind(itemView)
        }
    }
}