package ru.kalistratov.template.beauty.domain.service

interface AuthSettingsService {
    fun getToken(): String
    fun updateToken(token: String)

    fun getUser(): String?
    fun updateUser(user: String, token: String)

    fun exit()
}
