package ru.kalistratov.template.beauty.domain.service

interface AuthSettingsService {
    fun updateToken(token: String)
    fun loadToken(): String

    fun getUser(): String?
    fun updateUser(user: String, token: String)
}
