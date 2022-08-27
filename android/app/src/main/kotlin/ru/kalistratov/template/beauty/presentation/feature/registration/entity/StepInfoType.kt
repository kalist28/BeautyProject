package ru.kalistratov.template.beauty.presentation.feature.registration.entity

enum class StepInfoType {
    EMAIL,
    PASSWORD,
    FIRSTNAME,
    LASTNAME,
    PATRONYMIC
}

data class StepTypedInfo(
    val type: StepInfoType,
    val value: String
)
