package ru.kalistratov.template.beauty.domain.entity

data class Service(
    val id: Long,
    val title: String
)

data class OfferCategory(
    var id: Id,
    val title: String = "",
    val description: String = "",
    val types: List<OfferType> = emptyList(),
    val children: List<OfferCategory> = emptyList()
)

data class OfferType(
    val id: Id,
    val name: String = "",
    val description: String = "",
)