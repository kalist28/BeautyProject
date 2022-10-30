package ru.kalistratov.template.beauty.presentation.entity

data class RequestPermission(
    val code: Int,
    val permissions: List<String>
)

data class RequestPermissionsResult(
    val code: Int,
    val granted: Boolean
)
