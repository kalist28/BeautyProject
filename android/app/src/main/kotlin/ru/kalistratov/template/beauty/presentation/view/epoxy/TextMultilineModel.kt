package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemTextMultilineBinding
import ru.kalistratov.template.beauty.presentation.extension.setTextWithChecking
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

class TextMultilineModel(
    private val id: String,
    private val text: String?,
    private val changes: (String) -> Unit,
    private val marginsBundle: MarginsBundle? = null,
    @StringRes
    private val hintId: Int? = null,
) : EpoxyModelWithHolder<TextMultilineModel.Holder>() {

    init {
        id("multiline_${id}")
    }

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        editText.setTextWithChecking(text)
    }

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun getDefaultLayout() = R.layout.list_item_text_multiline

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTextMultilineBinding
        override fun bindView(v: View) = ListItemTextMultilineBinding.bind(v).run {
            hintId?.let(inputLayout::setHint)
            marginsBundle?.let(root::setMargins)
            editText.addTextChangedListener {
                it?.toString()?.let(changes::invoke)
            }
            binding = this
        }
    }
}