package ru.kalistratov.template.beauty.domain.di

import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.BuildConfig
import ru.kalistratov.template.beauty.domain.repository.api.ApiAuthRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiSequenceRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiUserRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiWorkdayWindowRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.infrastructure.repository.api.ApiAuthRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.repository.api.ApiSequenceRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.repository.api.ApiUserRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.repository.api.ApiWorkdayWindowRepositoryImpl
import javax.inject.Singleton

@Module
class ApiModule {

    private val uri = BuildConfig.SERVER_URI

    @Provides
    @Singleton
    fun provideApiAuthRepository(
        authSettingsService: AuthSettingsService
    ): ApiAuthRepository = ApiAuthRepositoryImpl(
        uri, authSettingsService
    )

    @Provides
    @Singleton
    fun provideApiSequenceRepository(
        authSettingsService: AuthSettingsService
    ): ApiSequenceRepository = ApiSequenceRepositoryImpl(
        uri, authSettingsService
    )

    @Provides
    @Singleton
    fun provideApiUserRepository(
        authSettingsService: AuthSettingsService
    ): ApiUserRepository = ApiUserRepositoryImpl(
        uri, authSettingsService
    )

    @Provides
    @Singleton
    fun provideApiWorkdayWindowRepository(
        authSettingsService: AuthSettingsService
    ): ApiWorkdayWindowRepository = ApiWorkdayWindowRepositoryImpl(
        uri, authSettingsService
    )
}
