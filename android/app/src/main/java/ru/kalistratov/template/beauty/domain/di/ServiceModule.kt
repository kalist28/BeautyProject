package ru.kalistratov.template.beauty.domain.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import ru.kalistratov.template.beauty.BuildConfig
import ru.kalistratov.template.beauty.domain.Application
import ru.kalistratov.template.beauty.domain.service.*

@Module
class ServiceModule(val application: Application) {

    @Provides
    @Singleton
    fun provideSessionManager(authSettingsService: AuthSettingsService): SessionManager =
        SessionManagerImpl(application, authSettingsService)

    @Provides
    @Singleton
    fun provideRegistrationService(): AuthService = AuthServiceImpl(BuildConfig.SERVER_URI)

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
