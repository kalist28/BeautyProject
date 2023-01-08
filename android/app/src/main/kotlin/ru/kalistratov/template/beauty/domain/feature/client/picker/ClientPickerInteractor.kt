package ru.kalistratov.template.beauty.domain.feature.client.picker

import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Id

interface ClientPickerInteractor {
    suspend fun getAll(): List<Client>
    suspend fun search(query: String): List<Client>

    suspend fun postClient(id: Id)
}