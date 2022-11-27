package ru.kalistratov.template.beauty.presentation.feature.personalarea.view

import android.view.View
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.entity.MenuItem
import ru.kalistratov.template.beauty.presentation.extension.getColorStateList
import ru.kalistratov.template.beauty.presentation.extension.getDrawable
import ru.kalistratov.template.beauty.presentation.feature.personalarea.entity.PersonalAreaMenuItemId

class PersonalAreaMenuController : EpoxyController() {
    var items: List<MenuItem> = emptyList()

    private val _clicks = mutableSharedFlow<Int>()
    val clicks: Flow<Int> = _clicks.asSharedFlow()

    override fun buildModels() {
        items.forEachIndexed { index, item ->
            MenuItemModel(item, _clicks::tryEmit).addTo(this)
            if (index != items.lastIndex) SeparatorModel(index)
                .addTo(this)
        }
    }
}

class MenuItemModel(
    private val item: MenuItem,
    private val clickAction: (Int) -> Unit
) : EpoxyModelWithHolder<MenuItemModel.ViewHolder>() {

    init {
        id(item.id)
    }

    override fun bind(holder: ViewHolder): Unit = with(holder) {
        root?.setOnClickListener { clickAction.invoke(item.id) }
        title?.let { it.text = item.title }
        icon?.let { it.setImageDrawable(it.getDrawable(item.iconId)) }
    }

    override fun getDefaultLayout() = R.layout.list_item_menu_with_badge

    override fun createNewHolder(parent: ViewParent) = ViewHolder()

    inner class ViewHolder : EpoxyHolder() {
        var root: View? = null
        var icon: ImageView? = null
        var title: TextView? = null
        override fun bindView(view: View) {
            root = view
            icon = view.findViewById(R.id.icon)
            title = view.findViewById(R.id.title_text_view)
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
