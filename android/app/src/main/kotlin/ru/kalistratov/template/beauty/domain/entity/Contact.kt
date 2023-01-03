package ru.kalistratov.template.beauty.domain.entity

data class Contact(
    val id: Id,
    val name: String,
    val number: String,
    val photoUri: String?
)
