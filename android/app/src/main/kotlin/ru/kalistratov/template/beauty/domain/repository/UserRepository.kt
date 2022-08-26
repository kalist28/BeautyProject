package ru.kalistratov.template.beauty.domain.repository

import ru.kalistratov.template.beauty.domain.entity.User

interface UserRepository {
    suspend fun getData(): User?
}
