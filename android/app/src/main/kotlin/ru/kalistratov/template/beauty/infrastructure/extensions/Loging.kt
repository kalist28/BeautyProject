package ru.kalistratov.template.beauty.infrastructure.extensions

import timber.log.Timber

fun Any.log(message: Any?) {
    Timber.tag("DEV_LOG ${javaClass.simpleName}")
    Timber.d("$message")
}

fun Any.loge(message: Any?) {
    Timber.tag("DEV_LOG ${javaClass.simpleName}")
    Timber.e("$message")
}
