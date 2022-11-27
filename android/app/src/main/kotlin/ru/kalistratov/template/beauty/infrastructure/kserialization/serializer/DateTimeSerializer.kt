package ru.kalistratov.template.beauty.infrastructure.kserialization.serializer

import com.soywiz.klock.Time
import com.soywiz.klock.parseTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.kalistratov.template.beauty.infrastructure.extensions.clockFormatPattern
import ru.kalistratov.template.beauty.infrastructure.extensions.clockTimeFormat

object ClockFormatTimeSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DateTimeClockFormat", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Time) =
        encoder.encodeString(value.format(clockFormatPattern))

    override fun deserialize(decoder: Decoder): Time =
        clockTimeFormat.parseTime(decoder.decodeString())
}
