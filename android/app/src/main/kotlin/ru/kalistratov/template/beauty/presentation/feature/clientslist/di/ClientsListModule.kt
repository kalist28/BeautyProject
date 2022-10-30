package ru.kalistratov.template.beauty.presentation.feature.clientslist.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.clientslist.ClientsListInteractor
import ru.kalistratov.template.beauty.domain.repository.ContactsRepository
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.clientslist.ClientsListInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.clientslist.ClientsListRouter
import ru.kalistratov.template.beauty.presentation.feature.clientslist.ClientsListRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.clientslist.view.ClientsListFragment
import ru.kalistratov.template.beauty.presentation.feature.clientslist.ClientsListViewModel

@Subcomponent(
    modules = [
        ClientsListModule::class,
        ClientsListPresentationModule::class,
    ]
)
interface ClientsListComponent {
    fun inject(fragment: ClientsListFragment)
}

@Module
class ClientsListModule(private val fragment: ClientsListFragment) {

    @Provides
    fun provideInteractor(
        clientsRepository: ContactsRepository
    ): ClientsListInteractor = ClientsListInteractorImpl(
        clientsRepository
    )

    @Provides
    fun provideRouter(): ClientsListRouter = ClientsListRouterImpl(
        fragment.javaClass.simpleName,
        fragment.findNavController()
    )
}

@Module
abstract class ClientsListPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ClientsListViewModel::class)
    abstract fun bindViewModel(viewModel: ClientsListViewModel): ViewModel
}
