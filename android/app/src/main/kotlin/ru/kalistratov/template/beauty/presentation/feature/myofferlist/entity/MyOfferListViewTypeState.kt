package ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity

import ru.kalistratov.template.beauty.domain.entity.*
import ru.kalistratov.template.beauty.presentation.entity.PriceContainer
import ru.kalistratov.template.beauty.presentation.entity.toContainer

sealed interface MyOfferListViewTypeState {

    companion object {
        private const val MAX_PRICE = 100_000
    }

    data class CreatingItem(
        val category: OfferCategory? = null,
        val type: OfferType? = null,
        val typeProperty: OfferTypeProperty? = null,
        val priceContainer: PriceContainer? = null,
        val description : String? = null,
    ) : MyOfferListViewTypeState {
        fun isAllowToSave(): Boolean {
            if (category == null) return false
            if (type == null) return false
            if (type.properties.isNotEmpty() && typeProperty == null) return false
            if (priceContainer == null) return false
            when (priceContainer) {
                is PriceContainer.Amount -> {
                    val price = priceContainer.value
                    if (price == 0 || price > MAX_PRICE) return false
                }
                is PriceContainer.Range -> {
                    val to = priceContainer.to
                    val from = priceContainer.from
                    if (from == 0 && to == 0) return false
                    if (from >= to) return false
                    if (from > MAX_PRICE || to > MAX_PRICE) return false
                }
                else -> Unit
            }

            return true
        }
    }

    data class EditingItem(
        val categoryTitle: String,
        val offerItem: OfferItem,
        val priceContainer: PriceContainer,
        val description: String?,
    ) : MyOfferListViewTypeState {
        fun isAllowToSave(): Boolean {
            if (description != offerItem.description) return true
            if (offerItem.price.toContainer() == priceContainer) return false
            when (priceContainer) {
                is PriceContainer.Amount -> {
                    val price = priceContainer.value
                    if (price == 0 || price > MAX_PRICE) return false
                }
                is PriceContainer.Range -> {
                    val to = priceContainer.to
                    val from = priceContainer.from
                    if (from == 0 && to == 0) return false
                    if (from >= to) return false
                    if (from > MAX_PRICE || to > MAX_PRICE) return false
                }
                else -> Unit
            }
            return true
        }
    }

    data class ListItems(
        val containers: List<OfferCategoryContainer>
    ) : MyOfferListViewTypeState
}