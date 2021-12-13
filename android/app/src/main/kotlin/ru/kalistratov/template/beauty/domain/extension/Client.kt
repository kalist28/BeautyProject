package ru.kalistratov.template.beauty.domain.extension

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import ru.kalistratov.template.beauty.infrastructure.extensions.jsonParser

fun getClient() = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(jsonParser)
    }
}
