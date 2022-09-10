package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.UserData

interface UserRepository {
    fun requestLoad()

    suspend fun get(): User?
    suspend fun update(data: UserData)
}
