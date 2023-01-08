package ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.view

import android.content.Context
import android.view.View
import android.view.ViewParent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.*
import ru.kalistratov.template.beauty.databinding.ListItemOfferCategoryHorizontalBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.entity.OfferTypeContainer
import ru.kalistratov.template.beauty.presentation.extension.toContentString
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity.MyOfferListViewTypeState
import ru.kalistratov.template.beauty.presentation.view.Margins
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins
import ru.kalistratov.template.beauty.presentation.view.setMargins

class MyOfferPickerController(
    private val context: Context,
    private val onBuildEnded: (Boolean) -> Unit
) : EpoxyController() {

    private companion object {
        val marginsBundle = MarginsBundle.base
        val propertyMarginsBundle = marginsBundle.copy(startMarginDp = 36)
    }

    private val itemsClicksMutableFlow = mutableSharedFlow<Id>()
    private val categoryClicksMutableFlow = mutableSharedFlow<Id>()

    var containers: Map<OfferCategory, List<OfferTypeContainer>> = emptyMap()
    var selectedCategory: Id? = null

    fun itemsClicks() = itemsClicksMutableFlow.asSharedFlow()
    fun categoryClicks() = categoryClicksMutableFlow.asSharedFlow()

    override fun buildModels() {
        val categoryId = selectedCategory
        if (categoryId == null) containers.keys.forEachIndexed { index, category ->
            buildCategory(category, index).addTo(this)
        } else containers.keys
            .find { it.id == categoryId }
            .let(containers::get)
            ?.let(::buildTypeContainers)

        onBuildEnded.invoke(selectedCategory == null)
    }

    private fun buildCategory(
        category: OfferCategory,
        index: Int = -1,
        forGrid: Boolean = true
    ) = OfferCategoryModel(
        category.id,
        category.title,
        forGrid,
        index,
        categoryClicksMutableFlow::tryEmit
    )

    private fun buildTypeContainers(
        containers: List<OfferTypeContainer>
    ) = containers.forEachIndexed { index, typeContainer ->
        when (typeContainer) {
            is OfferTypeContainer.Single -> buildContainerSingle(typeContainer)
            is OfferTypeContainer.WithProperties -> buildContainerWithProperties(typeContainer)
        }

        if (containers.lastIndex != index) divider {
            id("divider_$index")
            onBind { _, h, _ -> h.setMargins(marginsBundle) }
        }
    }

    private fun buildContainerSingle(container: OfferTypeContainer.Single) {
        val type = container.type
        val priceTextValue = container.price.toContentString(context)
        val listener = View.OnClickListener { itemsClicksMutableFlow.tryEmit(container.itemId) }
        typeWithPrice {
            id("type_${type.id}")
            name(type.name)
            price(priceTextValue)
            onBind { _, holder, _ ->
                holder.setMargins(marginsBundle)
                holder.dataBinding.root.setOnClickListener(listener)
            }
        }
    }

    private fun buildContainerWithProperties(container: OfferTypeContainer.WithProperties) {
        val type = container.type
        text {
            id("type_${type.id}")
            text(type.name)
            onBind { _, holder, _ -> holder.setMargins(marginsBundle) }
        }
        container.properties.forEach { propertyContainer ->
            val property = propertyContainer.property
            val priceTextValue = propertyContainer.price.toContentString(context)
            val listener = View.OnClickListener {
                itemsClicksMutableFlow.tryEmit(propertyContainer.itemId)
            }
            typePropertyWithPrice {
                id("type_property_${property.id}")
                name(property.name)
                price(priceTextValue)
                onBind { _, holder, _ ->
                    holder.setMargins(propertyMarginsBundle)
                    holder.dataBinding.root.setOnClickListener(listener)
                }
            }
        }
    }

}

data class OfferCategoryModel(
    private val id: Id,
    private val title: String,
    private val forGrid: Boolean,
    private val index: Int,
    private val clickAction: (Id) -> Unit
) : EpoxyModelWithHolder<OfferCategoryModel.Holder>() {

    init {
        id(id + title)
    }

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        title.text = this@OfferCategoryModel.title
        iconContainer.setImageDrawable(
            ContextCompat.getDrawable(
                root.context,
                when (this@OfferCategoryModel.title) {
                    "Брови" -> R.drawable.ic_brows
                    "Маникюр" -> R.drawable.ic_manicure
                    else -> R.drawable.ic_clean
                }
            )
        )

        root.apply {
            setOnClickListener { clickAction.invoke(this@OfferCategoryModel.id) }
            layoutParams = root.layoutParams.also {
                it.width = when (forGrid) {
                    true -> ConstraintLayout.LayoutParams.MATCH_PARENT
                    false -> ConstraintLayout.LayoutParams.WRAP_CONTENT
                }
            }
            if (index < 0) return@apply
            val verticalMargin = Margins.BASE_VERTICAL
            val horizontalMargin = when (index % 2) {
                0 -> Margins.BASE_HORIZONTAL to Margins.SMALL_HORIZONTAL
                else -> Margins.SMALL_HORIZONTAL to Margins.BASE_HORIZONTAL
            }
            setMargins(
                horizontalMargin.first, verticalMargin,
                horizontalMargin.second, verticalMargin
            )
        }

    }

    override fun getDefaultLayout() = R.layout.list_item_offer_category_horizontal

    override fun createNewHolder(parent: ViewParent) = Holder()

    class Holder : EpoxyHolder() {
        lateinit var binding: ListItemOfferCategoryHorizontalBinding
        override fun bindView(itemView: View) {
            binding = ListItemOfferCategoryHorizontalBinding.bind(itemView)
        }
    }
}