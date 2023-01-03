package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.content.res.Configuration
import android.view.View
import android.view.ViewParent
import androidx.core.widget.addTextChangedListener
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.common.RegexPattern
import ru.kalistratov.template.beauty.databinding.ListItemTextBinding
import ru.kalistratov.template.beauty.presentation.extension.setTextWithChecking
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.processor.TextFieldErrorProcessor
import ru.kalistratov.template.beauty.presentation.view.setMargins
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

data class PhoneNumberTextFieldModel(
    private val number: String?,
    private val changes: (String) -> Unit,
    private val marginsBundle: MarginsBundle? = null,
    private val id: String = "",
    private val errorProcessor: TextFieldErrorProcessor? = null,
) : EpoxyModelWithHolder<PhoneNumberTextFieldModel.Holder>() {

    init {
        id("text_field_phone_number_${id}")
    }

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        if (!number.isNullOrBlank()) editText.setTextWithChecking(number)
    }

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun getDefaultLayout() = R.layout.list_item_text

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTextBinding
        override fun bindView(v: View) = ListItemTextBinding.bind(v).run {
            inputLayout.setHint(R.string.phone_number)

            marginsBundle?.let(root::setMargins)
            editText.apply {
                setRawInputType(Configuration.KEYBOARD_12KEY)
                addTextChangedListener { it?.toString()?.let(changes::invoke) }
                errorProcessor?.setup(editText, inputLayout)

                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && text.isNullOrBlank()) setText("9")
                }
                MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
                    .let(::MaskFormatWatcher)
                    .installOn(this)
            }
            binding = this
        }
    }
}