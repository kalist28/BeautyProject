package ru.kalistratov.template.beauty.infrastructure.extensions

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.ClockFormatTimeSerializer
import ru.kalistratov.template.beauty.infrastructure.kserialization.serializer.WeekDaySerializer

@OptIn(ExperimentalSerializationApi::class)
internal val jsonParser = Json {
    explicitNulls = false

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
        contextual(ClockFormatTimeSerializer)
    }
}
