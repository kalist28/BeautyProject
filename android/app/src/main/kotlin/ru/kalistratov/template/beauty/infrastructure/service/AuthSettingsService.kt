package ru.kalistratov.template.beauty.infrastructure.service

import com.russhwolf.settings.Settings
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import javax.inject.Inject

class AuthSettingsServiceImpl @Inject constructor(
    private val settings: Settings
) : AuthSettingsService {

    companion object {
        private const val USER_ID_TAG = "user_id_tag"
        private const val TOKEN_TAG = "token_tag"
        private const val REFRESH_TOKEN_TAG = "refresh_token_tag"
    }

    override fun getToken(): String = settings.getString(TOKEN_TAG)

    override fun updateToken(token: String) = settings.putString(TOKEN_TAG, token)

    override fun getRefreshToken(): String =
        settings.getString(REFRESH_TOKEN_TAG)

    override fun updateRefreshToken(token: String) =
        settings.putString(REFRESH_TOKEN_TAG, token)

    override fun getUserId(): String? = settings.getStringOrNull(USER_ID_TAG)

    override fun updateUserId(user: String) = settings.putString(USER_ID_TAG, user)

    override fun exit() = with(settings) {
        remove(USER_ID_TAG)
        remove(TOKEN_TAG)
    }
}
