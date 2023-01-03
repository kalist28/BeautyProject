package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemTextBinding
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.processor.TextFieldErrorProcessor
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class TextFieldModel(
    private val id: String,
    private val text: String?,
    private val changes: (String) -> Unit,
    private val marginsBundle: MarginsBundle? = null,
    @StringRes private val hintId: Int? = null,
    private val errorProcessor: TextFieldErrorProcessor? = null,
) : EpoxyModelWithHolder<TextFieldModel.Holder>() {

    init {
        id("text_field_${id}")
    }

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        editText.setTextWithChecking(text)
    }

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun getDefaultLayout() = R.layout.list_item_text

    private fun EditText.setTextWithChecking(text: String?) {
        if (this.text?.toString() != text) setText(text)
    }

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTextBinding
        override fun bindView(v: View) = ListItemTextBinding.bind(v).run {
            loge("bind")
            hintId?.let(inputLayout::setHint)
            errorProcessor?.setup(editText, inputLayout)
            marginsBundle?.let(root::setMargins)
            editText.addTextChangedListener { it?.toString()?.let(changes::invoke) }
            binding = this
        }
    }
}