package ru.kalistratov.template.beauty.infrastructure.base



sealed interface ViewNotification {
    data class Toast(
        val message: String,
        val showLong: Boolean,
    ) : ViewNotification
}