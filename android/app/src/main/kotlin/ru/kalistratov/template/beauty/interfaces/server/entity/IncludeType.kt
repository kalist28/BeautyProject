package ru.kalistratov.template.beauty.interfaces.server.entity

sealed class IncludeType(val value: String?) {
    object Empty : IncludeType(null)
    class Custom(value : String) : IncludeType(value)

    object Types : IncludeType("types")
    object ChildrenTypes : IncludeType("children.types")
    object AllTypes : IncludeType(valueOfTypes(Types, ChildrenTypes))

    object Item : IncludeType("item")
    object ItemType : IncludeType("item.type")
    object ItemTypeProperty : IncludeType("item.type_property")

    object Properties : IncludeType("properties")

    object WorkdayWindow : IncludeType("workday_window")

    object Type : IncludeType("type")
    object TypeProperties : IncludeType("type_property")

    companion object {
        fun valueOfTypes(vararg types: IncludeType): String {
            var result = ""
            types.forEachIndexed { index, type ->
                result += type.value
                if (index != types.lastIndex) result += ","
            }
            return result
        }

        fun typeOfTypes(vararg types: IncludeType): Custom {
            var result = ""
            types.forEachIndexed { index, type ->
                result += type.value
                if (index != types.lastIndex) result += ","
            }
            return Custom(result)
        }
    }
}