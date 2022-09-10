package ru.kalistratov.template.beauty.domain.repository.api

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.request.UpdateUserRequest

interface ApiUserRepository {
    suspend fun getUser(id: String? = null): NetworkResult<User>
    suspend fun updateUser(request: UpdateUserRequest): NetworkResult<User>
}
