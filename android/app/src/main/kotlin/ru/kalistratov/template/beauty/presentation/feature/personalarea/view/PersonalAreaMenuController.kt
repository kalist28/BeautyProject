package ru.kalistratov.template.beauty.presentation.feature.personalarea.view

import android.view.View
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemMenuWithBadgeBinding
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.entity.MenuItem
import ru.kalistratov.template.beauty.presentation.extension.getDrawable
import ru.kalistratov.template.beauty.indent
import ru.kalistratov.template.beauty.indentSmall

class PersonalAreaMenuController : EpoxyController() {
    var items: List<MenuItem> = emptyList()

    private val _clicks = mutableSharedFlow<Int>()
    val clicks: Flow<Int> = _clicks.asSharedFlow()

    override fun buildModels() {
        items.forEachIndexed { index, item ->
            when(item) {
                is MenuItem.Container -> {
                    MenuItemModel(item, _clicks::tryEmit).addTo(this)
                    indentSmall { id("indentSmall_1") }
                }
                is MenuItem.Indent -> indent { id("step_$index") }
            }
        }
    }
}

class MenuItemModel(
    private val item: MenuItem.Container,
    private val clickAction: (Int) -> Unit
) : EpoxyModelWithHolder<MenuItemModel.ViewHolder>() {

    init {
        id(item.id)
    }

    override fun bind(holder: ViewHolder): Unit = with(holder.binding) {
        root.setOnClickListener { clickAction.invoke(item.id) }
        titleTextView.text = item.title
        iconContainer.let { it.setImageDrawable(it.getDrawable(item.iconId)) }
    }

    override fun getDefaultLayout() = R.layout.list_item_menu_with_badge

    override fun createNewHolder(parent: ViewParent) = ViewHolder()

    inner class ViewHolder : EpoxyHolder() {
        lateinit var binding: ListItemMenuWithBadgeBinding
        override fun bindView(view: View) {
            binding = ListItemMenuWithBadgeBinding.bind(view)
        }
    }
}

class SeparatorModel(id: Int) : EpoxyModelWithHolder<SeparatorModel.ViewHolder>() {

    init {
        id(id)
    }

    override fun getDefaultLayout() = R.layout.view_menu_separator

    override fun createNewHolder(parent: ViewParent) = ViewHolder()

    inner class ViewHolder : EpoxyHolder() {
        override fun bindView(itemView: View) = Unit
    }
}
