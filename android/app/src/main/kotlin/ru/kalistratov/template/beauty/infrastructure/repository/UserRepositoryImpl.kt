package ru.kalistratov.template.beauty.infrastructure.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.UserData
import ru.kalistratov.template.beauty.domain.entity.request.UpdateUserRequest
import ru.kalistratov.template.beauty.domain.extension.doIfSuccess
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.service.api.ApiUserService
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class UserRepositoryImpl(
    private val apiUserService: ApiUserService
) : UserRepository {

    private var data: User? = null

    private val mutableSharedFlow = mutableSharedFlow<Unit>()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)


    init {
        mutableSharedFlow
            .onEach { get() }
            .launchIn(scope)
    }

    override suspend fun get(): User? {
        if (data == null) data = loadData()
        return data
    }

    override suspend fun update(data: UserData) {
        apiUserService.updateUser(
            data.run { UpdateUserRequest(name, surname, patronymic) }
        ).doIfSuccess { this.data = it }
    }

    override fun requestLoad() {
        mutableSharedFlow.tryEmit(Unit)
    }

    private suspend fun loadData() = when (val response = apiUserService.getUser()) {
        is NetworkResult.Success -> {
            response.value
        }
        else -> {
            loge(response); null
        }
    }
}
