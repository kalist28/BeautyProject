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
import ru.kalistratov.template.beauty.presentation.entity.MenuItem
import ru.kalistratov.template.beauty.presentation.extension.getDrawable

class PersonalAreaMenuController : EpoxyController() {

    var clickAction: (Int) -> Unit = { }

    var items: List<MenuItem> = emptyList()

    fun clicks(): Flow<Int> = callbackFlow {
        clickAction = { trySend(it) }
        awaitClose { /* no operation */ }
    }.conflate()

    override fun buildModels() {
        items.forEachIndexed { index, item ->
            MenuItemModel(item).addTo(this)
            if (index != items.lastIndex) SeparatorModel(index)
                .addTo(this)
        }
    }

    inner class MenuItemModel(
        private val item: MenuItem
    ) : EpoxyModelWithHolder<MenuItemModel.ViewHolder>() {

        init {
            id(item.id)
        }

        override fun bind(holder: ViewHolder): Unit = with(holder) {
            root?.setOnClickListener { clickAction.invoke(item.id) }
            title?.let { it.text = item.title }
            icon?.let { it.setImageDrawable(it.getDrawable(item.iconId)) }
        }

        override fun getDefaultLayout() = R.layout.personal_area_menu_item

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
