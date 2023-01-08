package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.text.InputType
import android.view.View
import android.view.ViewParent
import android.widget.EditText
import androidx.annotation.StringRes
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemDateTimeBinding
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class DateTimeModel(
    private val id: String = "",
    private val date: String?,
    private val time: String?,
    private val dateClickAction: () -> Unit = {},
    private val timeClickAction: () -> Unit = {},
    private val marginsBundle: MarginsBundle? = null,
    @StringRes private val timeHint: Int? = null
) : EpoxyModelWithHolder<DateTimeModel.Holder>() {

    init {
        id("date_time_${id}")
    }

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        loge(date)
        dateEditText.setText(date)
        timeEditText.setText(time)
    }

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun getDefaultLayout() = R.layout.list_item_date_time

    private fun EditText.setOnlyClickable(clickAction: () -> Unit) {
        isClickable = true
        inputType = InputType.TYPE_NULL

        setOnClickListener { clickAction.invoke() }
        setOnFocusChangeListener { _, focus ->
            if (!focus) return@setOnFocusChangeListener
            clearFocus()
            clickAction.invoke()
        }
    }

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemDateTimeBinding
        override fun bindView(v: View) = ListItemDateTimeBinding.bind(v).run {
            marginsBundle?.let(root::setMargins)
            dateInputLayout.setHint(R.string.date)
            timeInputLayout.setHint(timeHint ?: R.string.time)
            listOf(dateInputLayout, timeInputLayout)
                .forEach { it.isHintAnimationEnabled = false }
            dateEditText.setOnlyClickable(dateClickAction)
            timeEditText.setOnlyClickable(timeClickAction)

            binding = this
        }
    }
}