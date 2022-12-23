package ru.kalistratov.template.beauty.presentation.feature.offerpicker.view

import android.view.View
import android.view.ViewParent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.*
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemOfferCategoryVerticalBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.OfferCategory
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.view.Margins
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins
import ru.kalistratov.template.beauty.presentation.view.setMargins
import ru.kalistratov.template.beauty.text
import ru.kalistratov.template.beauty.title


class OfferPickerController(
    private val onBuildEnded: (Boolean) -> Unit
) : EpoxyController() {

    init {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
    }

    var selected: List<Id> = emptyList()
    var categories: List<OfferCategory> = emptyList()

    val typeClicks = mutableSharedFlow<Id>()
    val categoryClicks = mutableSharedFlow<Id>()

    private var needClean = false

    private val carouselPaging = Carousel.Padding
        .dp(16, 0, 16, 0, 0)

    override fun buildModels() {
        if (needClean) return
        if (selected.isEmpty()) add(categories.mapIndexed { i, it -> bCategory(it, true, i) })
        else {
            val category = categories.lastOrNull() ?: return
            val carouselModals = category.children
                .map { bCategory(it, false) }
            val paging = carouselPaging
            carousel {
                id("carousel")
                padding(paging)
                models(carouselModals)
                hasFixedSize(false)
            }

            if (carouselModals.isNotEmpty()) title {
                id("categories_title")
                titleText("Услуги")
            }

            category.types.forEach { type ->
                val listener = View.OnClickListener { typeClicks.tryEmit(type.id) }
                text {
                    id(type.id)
                    text(type.name)
                    onBind { _, holder, _ ->
                        holder.setMargins(MarginsBundle.base)
                        holder.dataBinding.root.setOnClickListener(listener)
                    }
                }
            }
        }
        onBuildEnded.invoke(selected.isEmpty())
    }

    private fun bCategory(
        category: OfferCategory,
        forGrid: Boolean,
        index: Int = -1
    ) = OfferCategoryModel(
        category.id,
        category.title,
        forGrid,
        index,
        categoryClicks::tryEmit
    )
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
            val horizontalMargin = when (index % 3) {
                0 -> Margins.BASE_HORIZONTAL to Margins.SMALL_HORIZONTAL
                2 -> Margins.SMALL_HORIZONTAL to Margins.BASE_HORIZONTAL
                else -> Margins.SMALL_HORIZONTAL to Margins.SMALL_HORIZONTAL
            }
            setMargins(
                horizontalMargin.first, verticalMargin,
                horizontalMargin.second, verticalMargin
            )
        }

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