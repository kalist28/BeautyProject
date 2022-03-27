package ru.kalistratov.template.beauty.domain.service

interface AuthSettingsService {
    fun getToken(): String
    fun updateToken(token: String)

    fun getUserId(): String?
    fun updateUserId(user: String)

    fun exit()
}
