package ru.kalistratov.template.beauty.infrastructure.extensions

import kotlinx.serialization.json.Json

internal val jsonParser = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    encodeDefaults = true
    useAlternativeNames = true
    coerceInputValues = true
    allowStructuredMapKeys = true
    useArrayPolymorphism = true
}
