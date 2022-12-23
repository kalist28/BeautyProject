package ru.kalistratov.template.beauty.presentation.feature.offerpicker.entity

import ru.kalistratov.template.beauty.domain.entity.Id

sealed class OfferPickerType(val title: String) {
    object Category : OfferPickerType("category")
    data class Type(val id: Id) : OfferPickerType("type")
    //data class TypeProperty(val id: Id) : OfferPickerType("type_property")

    companion object {
        fun valueOf(title: String, id: Id?): OfferPickerType = when (title) {
            "type" -> Type(id!!)
            "category" -> Category
     //       "type_property" -> TypeProperty(id!!)
            else -> throw IllegalStateException("Title not found")
        }
    }
}