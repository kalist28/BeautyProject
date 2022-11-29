package ru.kalistratov.template.beauty.presentation.feature.offerlist.view

import android.view.View
import android.view.ViewParent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemOfferCategoryVerticalBinding
import ru.kalistratov.template.beauty.divider
import ru.kalistratov.template.beauty.text
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.title


class ServiceListController(
    private val onBuildEnded: (Boolean) -> Unit
) : EpoxyController() {

    init {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
    }

    var selected: List<Id> = emptyList()
    var categories: List<OfferCategory> = emptyList()

    val categoryClicks = mutableSharedFlow<Id>()

    private var needClean = false

    private val carouselPaging = Carousel.Padding
        .dp(16, 0, 16, 0, 0)

    override fun buildModels() {
        if (needClean) return
        if (selected.isEmpty()) add(categories.map { bCategory(it, true) })
        else {
            val category = categories.lastOrNull() ?: return
            val carouselModals = category.children
                .map { bCategory(it, false) }
            loge(category.types)
            val paging = carouselPaging
            carousel {
                id("carousel")
                padding(paging)
                models(carouselModals)
                hasFixedSize(false)
            }

            title {
                id("categories_title")
                titleText("Услуги")
            }
            val lastTypeIndex = category.types.lastIndex
            category.types.forEach {
                text {
                    id(it.id)
                    text(it.name)
                    //TODO Свой класс
                    padding(Carousel.Padding.dp(16, 0, 16, 8, 0))
                }
            }
        }
        onBuildEnded.invoke(selected.isEmpty())
    }

    private fun bCategory(
        category: OfferCategory,
        forGrid: Boolean
    ) = OfferCategoryModel(
        category.id,
        category.title,
        forGrid,
        categoryClicks::tryEmit
    )
}

data class OfferCategoryModel(
    private val id: Id,
    private val title: String,
    private val forGrid: Boolean,
    private val clickAction: (Id) -> Unit
) : EpoxyModelWithHolder<OfferCategoryModel.Holder>() {

    init {
        id(id + title)
    }

    override fun bind(holder: Holder) = with(holder.binding) {
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

        root.layoutParams = root.layoutParams.also {
            it.width = when (forGrid) {
                true -> ConstraintLayout.LayoutParams.MATCH_PARENT
                false -> ConstraintLayout.LayoutParams.WRAP_CONTENT
            }
        }
        root.setOnClickListener { clickAction.invoke(id) }

    }

    override fun getDefaultLayout() = R.layout.list_item_offer_category_vertical

    override fun createNewHolder(parent: ViewParent) = Holder()

    class Holder : EpoxyHolder() {
        lateinit var binding: ListItemOfferCategoryVerticalBinding
        override fun bindView(itemView: View) {
            binding = ListItemOfferCategoryVerticalBinding.bind(itemView)
        }
    }
}