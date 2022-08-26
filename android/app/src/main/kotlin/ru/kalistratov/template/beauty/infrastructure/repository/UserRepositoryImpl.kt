package ru.kalistratov.template.beauty.infrastructure.repository

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiUserRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class UserRepositoryImpl(val api: ApiUserRepository) : UserRepository {

    private var data: User? = null

    override suspend fun getData(): User? {
        return loadData()
    }

    private suspend fun loadData() = when (val response = api.getData()) {
        is NetworkResult.Success -> {
            response.value
        }
        else -> {loge(response); null}
    }
}
