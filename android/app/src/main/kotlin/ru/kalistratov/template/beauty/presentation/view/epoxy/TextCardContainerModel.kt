package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemTextContainerCardBinding
import ru.kalistratov.template.beauty.presentation.extension.setDebouncedOnClickListener
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class TextCardContainerModel(
    private val id: String,
    private val hint: String? = null,
    private val title: String? = null,
    private val clickAction: () -> Unit = { },
    private val marginsBundle: MarginsBundle? = null,
    @DrawableRes private val drawable: Int? = null
) : EpoxyModelWithHolder<TextCardContainerModel.Holder>() {

    init {
        id("text_card_container_$id")
    }

    override fun getDefaultLayout() = R.layout.list_item_text_container_card

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun bind(holder: Holder): Unit = holder.binding.run {
        val context = root.context
        icContainer.setImageDrawable(
            drawable?.let { AppCompatResources.getDrawable(context, it) }
        )
        val textToColor = when {
            title != null -> title to R.color.textDefault
            hint != null -> hint to R.color.subtitle
            else -> null to R.color.textDefault
        }
        text.text = textToColor.first
        text.setTextColor(context.getColor(textToColor.second))
    }

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemTextContainerCardBinding
        override fun bindView(itemView: View) {
            binding = ListItemTextContainerCardBinding.bind(itemView).apply {
                marginsBundle?.let(root::setMargins)
                root.setDebouncedOnClickListener(block = clickAction)
            }
        }
    }
}