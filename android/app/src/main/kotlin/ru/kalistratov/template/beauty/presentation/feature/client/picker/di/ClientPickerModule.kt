package ru.kalistratov.template.beauty.presentation.feature.client.picker.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.client.picker.ClientPickerInteractor
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.client.picker.ClientPickerInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.client.picker.ClientPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.client.picker.ClientPickerRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.client.picker.ClientPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.client.picker.view.ClientPickerFragment

@Subcomponent(
    modules = [
        ClientPickerModule::class,
        ClientPickerBindsModule::class,
        ClientPickerPresentationModule::class,
    ]
)
interface ClientPickerComponent {
    fun inject(fragment: ClientPickerFragment)
}

@Module
class ClientPickerModule(private val fragment: ClientPickerFragment) {
    @Provides
    fun provideRouter(): ClientPickerRouter = ClientPickerRouterImpl(
        "ClientPickerFragment",
        fragment.findNavController()
    )
}

@Module
interface ClientPickerBindsModule {
    @Binds
    fun provideInteractor(impl: ClientPickerInteractorImpl): ClientPickerInteractor
}

@Module
abstract class ClientPickerPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ClientPickerViewModel::class)
    abstract fun bindViewModel(viewModel: ClientPickerViewModel): ViewModel
}
