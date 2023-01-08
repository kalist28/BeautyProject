package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.PhoneNumber

interface ClientsRepository {
    suspend fun add(client: Client)

    suspend fun get(id: Id): Client?
    suspend fun getAll(fromCache: Boolean = false): List<Client>
    suspend fun search(query: String?): List<Client>

    suspend fun contains(number: PhoneNumber): Boolean

    suspend fun remove(id: Id)
    suspend fun removeAll(ids: List<Id>)
}