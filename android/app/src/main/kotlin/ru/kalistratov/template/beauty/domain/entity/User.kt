package ru.kalistratov.template.beauty.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val email: String,
)

@Serializable
data class UserData(
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
) {
    fun contentChanged(user: User?): Boolean {
        if (user == null ) return false
        return name != user.name ||
                surname != user.surname ||
                patronymic != user.patronymic
    }

    fun equalsChangeableUserContent(user: User?): Boolean {
        if (user == null ) return false
        return name == user.name &&
                surname == user.surname &&
                patronymic == user.patronymic
    }
}