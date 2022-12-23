package ru.kalistratov.template.beauty.interfaces.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServerPrice(
    val currency: ServerCurrency,
    val exchange: ServerCurrency,
)

@Serializable
data class ServerCurrency(
    val value: Long,
    val symbol: String?,
    val text: String,
    val no_style: String,
    val rule: String,
)