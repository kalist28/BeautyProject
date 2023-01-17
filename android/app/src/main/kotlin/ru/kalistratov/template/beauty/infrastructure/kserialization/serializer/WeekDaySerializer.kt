package ru.kalistratov.template.beauty.infrastructure.kserialization.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.kalistratov.template.beauty.domain.entity.WeekDay

object WeekDaySerializer : KSerializer<WeekDay> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("day", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: WeekDay) =
        encoder.encodeString(value.index.toString())

    override fun deserialize(decoder: Decoder): WeekDay =
        WeekDay.fromIndex(decoder.decodeInt())
}