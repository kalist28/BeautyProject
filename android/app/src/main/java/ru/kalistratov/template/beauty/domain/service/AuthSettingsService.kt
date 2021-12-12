package ru.kalistratov.template.beauty.domain.service

import com.russhwolf.settings.Settings

interface AuthSettingsService {
    fun updateToken(token: String)
    fun loadToken(): String

    fun getUser(): String?
    fun updateUser(user: String, token: String)
}

class AuthSettingsServiceImpl(private val settings: Settings) : AuthSettingsService {

    companion object {
        private const val USER_TAG = "user_tag"
        private const val TOKEN_TAG = "token_tag"
    }

    override fun updateToken(token: String) =
        settings.putString(TOKEN_TAG, token)

    override fun loadToken(): String = settings.getString(TOKEN_TAG)

    override fun getUser(): String? = settings.getStringOrNull(USER_TAG)

    override fun updateUser(user: String, token: String) {
        settings.putString(USER_TAG, user)
        updateToken(token)
    }
}
