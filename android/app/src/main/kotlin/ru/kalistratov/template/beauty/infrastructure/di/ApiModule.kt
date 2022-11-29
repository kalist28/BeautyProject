package ru.kalistratov.template.beauty.infrastructure.di

import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.BuildConfig
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.infrastructure.service.api.*
import ru.kalistratov.template.beauty.interfaces.server.service.*
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

    @Provides
    @Singleton
    fun provideApiOfferCategoryService(
        authSettingsService: AuthSettingsService
    ): ApiOfferCategoryService = ApiOfferCategoryServiceImpl(
        uri, authSettingsService
    )
}
