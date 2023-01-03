package ru.kalistratov.template.beauty.infrastructure.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.domain.repository.ClientsRepository
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.domain.repository.OfferItemRepository
import ru.kalistratov.template.beauty.domain.repository.OfferTypeRepository
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.repository.ClientsRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.repository.OfferCategoryRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.repository.OfferItemRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.repository.OfferTypeRepositoryImpl
import ru.kalistratov.template.beauty.interfaces.server.service.ApiClientsService
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferCategoryService
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferItemService
import ru.kalistratov.template.beauty.interfaces.server.service.ApiOfferTypeService
import javax.inject.Singleton

@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun provideOfferCategoryRepository(impl: OfferCategoryRepositoryImpl): OfferCategoryRepository

    @Binds
    @Singleton
    fun provideOfferItemRepository(impl: OfferItemRepositoryImpl): OfferItemRepository

    @Binds
    @Singleton
    fun provideOfferTypeRepository(impl: OfferTypeRepositoryImpl): OfferTypeRepository

    @Binds
    @Singleton
    fun provideClientsRepository(impl: ClientsRepositoryImpl): ClientsRepository
}
