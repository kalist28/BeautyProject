package ru.kalistratov.template.beauty.domain.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.domain.repository.api.ApiAuthRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiUserRepository
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.service.AuthServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.AuthSettingsServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.SessionManagerImpl
import javax.inject.Singleton

@Module
class ServiceModule(val application: Application) {
    @Provides
    @Singleton
    fun provideSessionManager(authSettingsService: AuthSettingsService): SessionManager =
        SessionManagerImpl(application, authSettingsService)

    @Provides
    @Singleton
    fun provideRegistrationService(
        apiAuthRepository: ApiAuthRepository,
        apiUserRepository: ApiUserRepository,
        authSettingsService: AuthSettingsService
    ): AuthService = AuthServiceImpl(
        apiAuthRepository,
        apiUserRepository,
        authSettingsService
    )

    @Provides
    @Singleton
    fun provideAuthSettings(application: Application): Settings = AndroidSettings(
        application.getSharedPreferences("auth_settings", Context.MODE_PRIVATE)
    )

    @Provides
    @Singleton
    fun provideAuthSettingsService(authSettings: Settings): AuthSettingsService =
        AuthSettingsServiceImpl(authSettings)
}
