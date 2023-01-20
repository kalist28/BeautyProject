package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.view.View
import android.view.ViewParent
import androidx.annotation.StringRes
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemSwitchBinding
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class SwitchModel(
    private val id: String,
    private val title: String,
    private val checked: Boolean,
    private val changeAction: (Boolean) -> Unit,
    private val marginsBundle: MarginsBundle? = null
) : EpoxyModelWithHolder<SwitchModel.Holder>() {

    init {
        id("switch_$id")
    }

    override fun getDefaultLayout() = R.layout.list_item_switch

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun bind(holder: Holder) = holder.binding.materialSwitch.run {
        text = title
        isChecked = checked
    }

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemSwitchBinding
        override fun bindView(itemView: View) {
            binding = ListItemSwitchBinding.bind(itemView).apply {
                with(materialSwitch) {
                    marginsBundle?.let(::setMargins)
                    setOnCheckedChangeListener { _, checked ->
                        changeAction.invoke(checked)
                    }
                }
            }
        }
    }
}