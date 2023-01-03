package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.text.InputType
import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemTextContainerBinding
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class TextContainerModel(
    private val id: String,
    private val hint: String? = null,
    private val title: String? = null,
    private val clickAction: () -> Unit = { },
    private val marginsBundle: MarginsBundle? = null
) : EpoxyModelWithHolder<TextContainerModel.Holder>() {

    init {
        id("text_container_$id")
    }

    override fun getDefaultLayout() = R.layout.list_item_text_container

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun bind(holder: Holder): Unit = holder.binding.run {
        marginsBundle?.let(root::setMargins)
        hint?.let(container::setHint)
        container.isHintAnimationEnabled = false
        text.apply {
            setText(title)
            isClickable = true
            inputType = InputType.TYPE_NULL

            setOnClickListener { clickAction() }
            setOnFocusChangeListener { _, focus ->
                if (!focus) return@setOnFocusChangeListener
                clearFocus()
                clickAction()
            }
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTextContainerBinding
        override fun bindView(itemView: View) {
            binding = ListItemTextContainerBinding.bind(itemView)
        }
    }
}