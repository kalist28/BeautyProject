package ru.kalistratov.template.beauty.presentation.feature.servicelist.view

import android.content.Context
import android.view.View
import android.view.ViewParent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemMenuWithBadgeBinding
import ru.kalistratov.template.beauty.domain.entity.Service
import ru.kalistratov.template.beauty.infrastructure.base.Group
import ru.kalistratov.template.beauty.infrastructure.base.GroupItem
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow


class ServiceListController : EpoxyController() {

    var services: List<Group<Service>> = emptyList()

    val serviceClicks = mutableSharedFlow<Long>()

    override fun buildModels() {
        services.forEach { buildGroupModels(it) }
    }

    private fun buildGroupModels(
        group: Group<Service>
    ) {
        buildModel(
            id = group.id,
            isGroup = true,
            title = group.title,
        )
        group.items.forEach { element ->
            when (element) {
                is Group -> buildGroupModels(element)
                is GroupItem -> buildModel(
                    isGroup = false,
                    id = element.item.id,
                    title = element.item.title,
                )
            }
        }
    }

    private fun buildModel(
        id: Long,
        title: String,
        isGroup: Boolean,
    ) = ServiceModel(
        title = title,
        isGroup = isGroup,
        clickAction = { serviceClicks.tryEmit(id) }
    ).addTo(this)
}

class ServiceModel(
    private val isGroup: Boolean,
    private val title: String,
    private val clickAction: () -> Unit
) : EpoxyModelWithHolder<ServiceModel.Holder>() {

    companion object {
        private const val DURATION = 300L
        private var openAnim: Animation? = null
        private var closeAnim: Animation? = null

        private fun openAnim(context: Context): Animation {
            return openAnim ?: createAnimation(context, R.anim.open_menu)
                .apply { openAnim = this }
        }

        private fun closeAnim(context: Context): Animation {
            return closeAnim ?: createAnimation(context, R.anim.close_menu)
                .also { closeAnim = it }
        }

        private fun createAnimation(
            context: Context,
            @AnimRes res: Int
        ): Animation = AnimationUtils
            .loadAnimation(context, res)
            .apply {
                duration = DURATION
                fillAfter = true
            }
    }

    override fun getDefaultLayout() = R.layout.list_item_menu_with_badge

    init {
        id(title)
    }

    private var animCounter = 0

    override fun bind(
        holder: Holder
    ) = with(holder.binding) {
        titleTextView.text = title

        icon.isVisible = isGroup
        arrow.isVisible = isGroup

        root.setOnClickListener {
            clickAction.invoke()
            with(arrow) {
                startAnimation(
                    when (animCounter++ % 2 == 0) {
                        true -> openAnim(context)
                        false -> closeAnim(context)
                    }
                )
            }
        }
    }

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ServiceModel

        if (isGroup != other.isGroup) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + isGroup.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemMenuWithBadgeBinding
        override fun bindView(itemView: View) {
            binding = ListItemMenuWithBadgeBinding.bind(itemView)
        }
    }
}