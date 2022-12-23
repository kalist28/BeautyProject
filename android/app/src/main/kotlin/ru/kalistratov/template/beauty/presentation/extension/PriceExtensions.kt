package ru.kalistratov.template.beauty.presentation.extension

import android.content.Context
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.entity.Price
import ru.kalistratov.template.beauty.presentation.entity.PriceContainer

fun Price.toContentString(context: Context) = when (this) {
    is Price.ByAgreement -> context.getString(R.string.price_by_agreement)
    is Price.Amount -> "${value.currency.value} ${value.currency.symbol}"
    is Price.Range -> "${from.currency.value} - ${to.currency.value} ${from.currency.symbol}"
}
