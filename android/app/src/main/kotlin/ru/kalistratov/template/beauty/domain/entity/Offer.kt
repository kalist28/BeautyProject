package ru.kalistratov.template.beauty.domain.entity

data class OfferCategory(
    var id: Id,
    val parentId: Id?,
    val title: String = "",
    val description: String = "",
    val types: List<OfferType> = emptyList(),
    val children: List<OfferCategory> = emptyList()
)

data class OfferType(
    val id: Id,
    val categoryId: Id,
    val name: String = "",
    val description: String = "",
    val properties: List<OfferTypeProperty> = emptyList()
)

data class OfferTypeProperty(
    val id: Id,
    val typeId: Id = "",
    val name: String = "",
    val description: String = "",
)

data class OfferItem(
    val id: Id,
    val type: OfferType,
    val typeProperty: OfferTypeProperty?,
    val description: String = "",
    val price: Price,
    val published: Boolean,
) {

    fun getContentText() =
        if (typeProperty == null) type.name
        else "${type.name} | ${typeProperty.name}"
}

data class OfferItemDataBundle(
    val typeId: Id? = null,
    val typePropertyId: Id? = null,
    val description: String? = null,
    val priceFrom: Int,
    val priceTo: Int?,
    val published: Boolean = true,
)