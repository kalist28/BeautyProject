package ru.kalistratov.template.beauty.domain.extension

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

inline fun <T, R> NetworkResult<T>.doIfSuccess(block: (T) -> R): NetworkResult<T> = this
    .also { if (this is NetworkResult.Success) block.invoke(this.value) }

fun <T> NetworkResult<T>.logIfError(): NetworkResult<T> =
    this.also {
        if (this !is NetworkResult.GenericError) return@also
        loge("Network error: ${unicodeDecode(this.toString())}")
    }

fun unicodeDecode(unicode: String): String {
    val stringBuffer = StringBuilder()
    var i = 0
    while (i < unicode.length) {
        if (i + 1 < unicode.length)
            if (unicode[i].toString() + unicode[i + 1].toString() == "\\u") {
                val symbol = unicode.substring(i + 2, i + 6)
                val c = Integer.parseInt(symbol, 16)
                stringBuffer.append(c.toChar())
                i += 5
            } else stringBuffer.append(unicode[i])
        i++
    }
    return stringBuffer.toString()
}