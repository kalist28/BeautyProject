package ru.kalistratov.template.beauty.interfaces.server.entity

sealed class IncludeType(val value: String) {
    class Custom(value : String) : IncludeType(value)

    object Types : IncludeType("types")
    object ChildrenTypes : IncludeType("children.types")
    object AllTypes : IncludeType(valueOfTypes(Types, ChildrenTypes))

    object Properties : IncludeType("properties")

    companion object {
        private fun valueOfTypes(vararg types: IncludeType): String {
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