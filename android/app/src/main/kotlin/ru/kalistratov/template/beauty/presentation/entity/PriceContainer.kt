package ru.kalistratov.template.beauty.presentation.entity

import ru.kalistratov.template.beauty.domain.entity.Price

sealed interface PriceContainer {
    object ByAgreement : PriceContainer
    data class Amount(val value: Int) : PriceContainer
    data class Range(val from: Int, val to: Int) : PriceContainer
}

fun PriceContainer.getFrom() = when (this) {
    is PriceContainer.Range -> from
    is PriceContainer.Amount -> value
    else -> 0
}

fun PriceContainer.getTo() = when (this) {
    is PriceContainer.Range -> to
    else -> null
}

fun Price.toContainer() = when (this) {
    is Price.ByAgreement -> PriceContainer.ByAgreement
    is Price.Amount -> PriceContainer.Amount(value.currency.value.toInt())
    is Price.Range -> PriceContainer.Range(
        from.currency.value.toInt(),
        to.currency.value.toInt(),
    )
}