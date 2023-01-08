package ru.kalistratov.template.beauty.infrastructure.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.domain.feature.contactpicker.ContactPickerBroadcast
import ru.kalistratov.template.beauty.domain.repository.ContactsRepository
import ru.kalistratov.template.beauty.domain.service.*
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.feature.contactpicker.ContactPickerBroadcastImpl
import ru.kalistratov.template.beauty.infrastructure.repository.ContactsRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.service.*
import ru.kalistratov.template.beauty.interfaces.server.service.ApiAuthService
import ru.kalistratov.template.beauty.interfaces.server.service.ApiUserService
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [SettingsProvideModule::class])
interface ServiceModule {

    @Binds
    @Singleton
    fun provideSessionManager(impl: SessionManagerImpl): SessionManager

    @Binds
    @Singleton
    fun provideRegistrationService(impl: AuthServiceImpl): AuthService

    @Binds
    @Singleton
    fun provideAuthSettingsService(impl: AuthSettingsServiceImpl): AuthSettingsService

    @Binds
    @Singleton
    fun provideContactsRepository(impl: ContactsRepositoryImpl): ContactsRepository

    @Binds
    @Singleton
    fun bindsContactPickerBroadcast(impl: ContactPickerBroadcastImpl): ContactPickerBroadcast

    @Binds
    @Singleton
    fun providePermissionsService(impl: PermissionsServiceImpl): PermissionsService

    @Binds
    @Singleton
    fun provideMyOfferPickerService(impl: MyOfferPickerServiceImpl): MyOfferPickerService

    @Binds
    @Singleton
    fun provideClientPickerService(impl: ClientPickerServiceImpl): ClientPickerService
}

@Module
class SettingsProvideModule {
    @Provides
    @Singleton
    @Named("auth_settings")
    fun provideAuthSettings(application: Application): Settings = AndroidSettings(
        application.getSharedPreferences("auth_settings", Context.MODE_PRIVATE)
    )
}