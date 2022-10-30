package ru.kalistratov.template.beauty.infrastructure.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.domain.repository.ContactsRepository
import ru.kalistratov.template.beauty.domain.service.api.ApiAuthService
import ru.kalistratov.template.beauty.domain.service.api.ApiUserService
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.PermissionsService
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.repository.ContactsRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.service.AuthServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.AuthSettingsServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.PermissionsServiceImpl
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
        apiAuthService: ApiAuthService,
        apiUserService: ApiUserService,
        authSettingsService: AuthSettingsService
    ): AuthService = AuthServiceImpl(
        apiAuthService,
        apiUserService,
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

    @Provides
    @Singleton
    fun provideContactsRepository(
        permissionsService: PermissionsService
    ): ContactsRepository = ContactsRepositoryImpl(
        application,
        permissionsService
    )

    @Provides
    @Singleton
    fun providePermissionsService(): PermissionsService = PermissionsServiceImpl()
}
