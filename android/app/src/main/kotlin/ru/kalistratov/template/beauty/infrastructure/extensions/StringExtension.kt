package ru.kalistratov.template.beauty.infrastructure.extensions

fun String.replaceBlankToNull() = this.ifBlank { null }