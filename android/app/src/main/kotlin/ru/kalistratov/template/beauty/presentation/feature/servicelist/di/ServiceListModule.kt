package ru.kalistratov.template.beauty.presentation.feature.servicelist.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.servicelist.ServiceListInteractor
import ru.kalistratov.template.beauty.presentation.feature.servicelist.ServiceListInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.servicelist.ServiceListRouter
import ru.kalistratov.template.beauty.presentation.feature.servicelist.ServiceListRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.servicelist.ServiceListViewModel
import ru.kalistratov.template.beauty.presentation.feature.servicelist.view.ServiceListFragment

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

    ): ServiceListInteractor = ServiceListInteractorImpl(

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