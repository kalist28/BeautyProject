package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.User

interface UserRepository {
    fun requestLoad()

    suspend fun get(): User?
}
