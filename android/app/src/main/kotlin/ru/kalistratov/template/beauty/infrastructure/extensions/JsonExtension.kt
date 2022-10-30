package ru.kalistratov.template.beauty.infrastructure.extensions

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.WeekDaySerializer

internal val jsonParser = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    encodeDefaults = true
    useAlternativeNames = true
    coerceInputValues = true
    allowStructuredMapKeys = true
    useArrayPolymorphism = true
    serializersModule = SerializersModule {
        contextual(WeekDaySerializer)
    }
}
