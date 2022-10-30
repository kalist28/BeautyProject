package ru.kalistratov.template.beauty.infrastructure.di

import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.BuildConfig
import ru.kalistratov.template.beauty.domain.service.api.ApiAuthService
import ru.kalistratov.template.beauty.domain.service.api.ApiSequenceService
import ru.kalistratov.template.beauty.domain.service.api.ApiUserService
import ru.kalistratov.template.beauty.domain.service.api.ApiSequenceDayWindowsService
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.infrastructure.service.api.ApiAuthServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.api.ApiSequenceServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.api.ApiUserServiceImpl
import ru.kalistratov.template.beauty.infrastructure.service.api.ApiSequenceDayWindowsServiceImpl
import javax.inject.Singleton

@Module
class ApiModule {

    private val uri = BuildConfig.SERVER_URI

    @Provides
    @Singleton
    fun provideApiAuthService(
        authSettingsService: AuthSettingsService
    ): ApiAuthService = ApiAuthServiceImpl(
        uri, authSettingsService
    )

    @Provides
    @Singleton
    fun provideApiSequenceService(
        authSettingsService: AuthSettingsService
    ): ApiSequenceService = ApiSequenceServiceImpl(
        uri, authSettingsService
    )

    @Provides
    @Singleton
    fun provideApiUserService(
        authSettingsService: AuthSettingsService
    ): ApiUserService = ApiUserServiceImpl(
        uri, authSettingsService
    )

    @Provides
    @Singleton
    fun provideApiWorkdayWindowService(
        authSettingsService: AuthSettingsService
    ): ApiSequenceDayWindowsService = ApiSequenceDayWindowsServiceImpl(
        uri, authSettingsService
    )
}
