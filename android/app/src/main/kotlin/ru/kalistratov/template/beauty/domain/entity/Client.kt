package ru.kalistratov.template.beauty.domain.entity

data class Client(
    val id: Id,
    val name: String,
    val surname: String?,
    val patronymic: String?,
    val number: PhoneNumber,
    val note: String?
) {
    companion object {
        val EMPTY = Client(
            "",
            "",
            "",
            "",
            "",
            "",
        )
    }
}