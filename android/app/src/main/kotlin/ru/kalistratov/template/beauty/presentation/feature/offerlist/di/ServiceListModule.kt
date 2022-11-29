package ru.kalistratov.template.beauty.presentation.feature.offerlist.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.presentation.feature.offerlist.ServiceListInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.offerlist.ServiceListRouter
import ru.kalistratov.template.beauty.presentation.feature.offerlist.ServiceListRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.offerlist.ServiceListViewModel
import ru.kalistratov.template.beauty.presentation.feature.offerlist.view.ServiceListFragment
import ru.kalistratov.template.beauty.domain.feature.servicelist.ServiceListInteractor
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey

@Subcomponent(
    modules = [
        ServiceListModule::class,
        ServiceListPresentationModule::class
    ]
)
interface ServiceListComponent {
    fun inject(fragment: ServiceListFragment)
}

@Module
class ServiceListModule(private val fragment: ServiceListFragment) {

    @Provides
    fun provideInteractor(
        offerCategoryRepository: OfferCategoryRepository
    ): ServiceListInteractor = ServiceListInteractorImpl(
        offerCategoryRepository
    )

    @Provides
    fun provideRouter(): ServiceListRouter =
        ServiceListRouterImpl(fragment.findNavController())
}

@Module
abstract class ServiceListPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ServiceListViewModel::class)
    abstract fun bindViewModel(viewModel: ServiceListViewModel): ViewModel
}