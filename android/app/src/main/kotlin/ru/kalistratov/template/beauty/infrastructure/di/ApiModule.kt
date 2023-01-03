package ru.kalistratov.template.beauty.infrastructure.di

import dagger.Binds
import dagger.Module
import ru.kalistratov.template.beauty.infrastructure.service.api.*
import ru.kalistratov.template.beauty.interfaces.server.service.*
import javax.inject.Singleton

@Module
interface ApiModule {

    @Binds
    @Singleton
    fun provideApiAuthService(
        impl: ApiAuthServiceImpl
    ): ApiAuthService

    @Binds
    @Singleton
    fun provideApiClientsService(
        impl: ApiClientsServiceImpl
    ): ApiClientsService

    @Binds
    @Singleton
    fun provideApiSequenceService(
        impl: ApiSequenceServiceImpl
    ): ApiSequenceService

    @Binds
    @Singleton
    fun provideApiUserService(
        impl: ApiUserServiceImpl
    ): ApiUserService

    @Binds
    @Singleton
    fun provideApiWorkdayWindowService(
        impl: ApiSequenceDayWindowsServiceImpl
    ): ApiSequenceDayWindowsService

    @Binds
    @Singleton
    fun provideApiOfferCategoryService(
        impl: ApiOfferCategoryServiceImpl
    ): ApiOfferCategoryService

    @Binds
    @Singleton
    fun provideApiOfferItemService(
        impl: ApiOfferItemServiceImpl
    ): ApiOfferItemService

    @Binds
    @Singleton
    fun provideApiOfferTypeService(
        impl: ApiOfferTypeServiceImpl
    ): ApiOfferTypeService
}
