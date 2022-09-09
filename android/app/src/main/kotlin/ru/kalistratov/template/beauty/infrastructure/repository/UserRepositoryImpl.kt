package ru.kalistratov.template.beauty.infrastructure.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiUserRepository
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class UserRepositoryImpl(
    private val api: ApiUserRepository
) : UserRepository {

    private var data: User? = null

    private val mutableSharedFlow = mutableSharedFlow<Unit>()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)


    init {
        mutableSharedFlow
            .onEach { loadData() }
            .launchIn(scope)
    }

    override suspend fun get(): User? {
        if (data == null) data = loadData()
        return data
    }

    override fun requestLoad() {
        mutableSharedFlow.tryEmit(Unit)
    }

    private suspend fun loadData() = when (val response = api.getUser()) {
        is NetworkResult.Success -> {
            response.value
        }
        else -> { loge(response); null }
    }
}
