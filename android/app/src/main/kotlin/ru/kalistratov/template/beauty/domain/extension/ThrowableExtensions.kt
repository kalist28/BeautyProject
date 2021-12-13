package ru.kalistratov.template.beauty.domain.extension

fun Throwable.getJson(): String? = message?.let { message ->
    val startIndex = message.indexOfFirst { it == '"' } + 1
    val endIndex = message.indexOfLast { it == '"' }
    if (startIndex < 0 || endIndex < 0) null
    else message.substring(startIndex, endIndex)
}
