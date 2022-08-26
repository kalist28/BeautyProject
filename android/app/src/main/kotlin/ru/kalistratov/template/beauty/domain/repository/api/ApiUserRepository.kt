package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.User

interface ApiUserRepository {
    suspend fun getUser(id: String): NetworkResult<User>
    suspend fun getData(): NetworkResult<User>
}
