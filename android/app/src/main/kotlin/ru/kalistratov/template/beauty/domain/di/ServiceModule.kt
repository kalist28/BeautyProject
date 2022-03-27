package ru.kalistratov.template.beauty.domain.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.BuildConfig
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.domain.service.*
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.repository.ApiRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.service.AuthServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.AuthSettingsServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.SessionManagerImpl
import javax.inject.Singleton

@Module
class ServiceModule(val application: Application) {

    @Provides
    @Singleton
    fun provideApiRepository(
        authSettingsService: AuthSettingsService
    ): ApiRepository = ApiRepositoryImpl(
        BuildConfig.SERVER_URI,
        authSettingsService
    )

    @Provides
    @Singleton
    fun provideSessionManager(authSettingsService: AuthSettingsService): SessionManager =
        SessionManagerImpl(application, authSettingsService)

    @Provides
    @Singleton
    fun provideRegistrationService(
        apiRepository: ApiRepository,
        authSettingsService: AuthSettingsService
    ): AuthService = AuthServiceImpl(
        apiRepository,
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
