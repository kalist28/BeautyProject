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
import ru.kalistratov.template.beauty.presentation.extension.setDebouncedOnClickListener
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.PaddingBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins
import ru.kalistratov.template.beauty.presentation.view.updatePadding

data class TimeRangeViewModel(
    private val id: String,
    private val startTimeSource: TimeSource,
    private val finishTimeSource: TimeSource,
    private val clickAction: (TimeSourceType) -> Unit,
    private val paddingBundle: PaddingBundle? = null,
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
        setError(holder.binding.error)


        startEditText.setText(startTimeSource.time.toClockFormat())
        finishEditText.setText(finishTimeSource.time.toClockFormat())

        startHintId?.let(startInputLayout::setHint)
        finishHintId?.let(finishInputLayout::setHint)
    }

    private fun EditText.initTimeEditFile(source: TimeSource) {
        isClickable = true
        inputType = InputType.TYPE_NULL

        setDebouncedOnClickListener { clickAction(source.type) }
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

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTimeRangeBinding
        override fun bindView(itemView: View) {
            binding = ListItemTimeRangeBinding.bind(itemView).apply {
                marginsBundle?.let(rootContainer::setMargins)
                paddingBundle?.let(rootContainer::updatePadding)

                startEditText.initTimeEditFile(startTimeSource)
                finishEditText.initTimeEditFile(finishTimeSource)
            }
        }
    }
}