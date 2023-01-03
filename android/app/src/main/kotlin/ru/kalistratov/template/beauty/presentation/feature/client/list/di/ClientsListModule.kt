package ru.kalistratov.template.beauty.presentation.feature.client.list.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.clientslist.ClientsListInteractor
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.client.list.ClientsListInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.client.list.ClientsListRouter
import ru.kalistratov.template.beauty.presentation.feature.client.list.ClientsListRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.client.list.ClientsListViewModel
import ru.kalistratov.template.beauty.presentation.feature.client.list.view.ClientsListFragment

@Subcomponent(
    modules = [
        ClientsListModule::class,
        ClientsListBindsModule::class,
        ClientsListPresentationModule::class,
    ]
)
interface ClientsListComponent {
    fun inject(fragment: ClientsListFragment)
}

@Module
class ClientsListModule(private val fragment: ClientsListFragment) {

    @Provides
    fun provideRouter(): ClientsListRouter = ClientsListRouterImpl(
        fragment.javaClass.simpleName,
        fragment.findNavController()
    )
}

@Module
interface ClientsListBindsModule {

    @Binds
    fun provideInteractor(impl: ClientsListInteractorImpl): ClientsListInteractor
}

@Module
abstract class ClientsListPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ClientsListViewModel::class)
    abstract fun bindViewModel(viewModel: ClientsListViewModel): ViewModel
}
