package ru.kalistratov.template.beauty.infrastructure.helper.mapper

import ru.kalistratov.template.beauty.domain.entity.Currency
import ru.kalistratov.template.beauty.domain.entity.PriceDetailing
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerCurrency
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerPrice

fun ServerPrice.toLocal() = PriceDetailing(
    currency = currency.toLocal(),
    exchange = exchange.toLocal()
)

fun ServerCurrency.toLocal() = Currency(
    value = value,
    symbol = symbol ?: "",
    text = text,
    noStyle = no_style,
    rule = rule
)