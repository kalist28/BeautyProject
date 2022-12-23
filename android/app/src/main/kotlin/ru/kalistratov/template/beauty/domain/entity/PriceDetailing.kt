package ru.kalistratov.template.beauty.domain.entity

sealed interface Price {
    object ByAgreement : Price
    data class Amount(val value: PriceDetailing) : Price
    data class Range(val from: PriceDetailing, val to: PriceDetailing) : Price
}

data class PriceDetailing(
    val currency: Currency,
    val exchange: Currency,
)

data class Currency(
    val value: Long,
    val symbol: String,
    val text: String,
    val noStyle: String,
    val rule: String,
)

fun Price.getFrom() = when (this) {
    is Price.Range -> from
    is Price.Amount -> value
    else -> null
}

fun Price.getTo() = when (this) {
    is Price.Range -> to
    else -> null
}

fun createPrice(from: PriceDetailing, to: PriceDetailing) =
    if (from.currency.value == 0L && to.currency.value == 0L)
        Price.ByAgreement
    else when (to.currency.value == 0L) {
        true -> Price.Amount(from)
        false -> Price.Range(from, to)
    }
