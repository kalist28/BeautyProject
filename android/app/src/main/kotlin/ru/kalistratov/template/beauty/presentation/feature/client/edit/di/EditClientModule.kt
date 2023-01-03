package ru.kalistratov.template.beauty.presentation.feature.client.edit.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.client.edit.EditClientInteractor
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.client.edit.*
import ru.kalistratov.template.beauty.presentation.feature.client.edit.view.EditClientFragment

@Subcomponent(
    modules = [
        EditClientModule::class,
        EditClientBindsModule::class,
        EditClientPresentationModule::class,
    ]
)
interface EditClientComponent {
    fun inject(fragment: EditClientFragment)
}

@Module
class EditClientModule(private val fragment: EditClientFragment) {
    @Provides
    fun provideRouter(): EditClientRouter = EditClientRouterImpl(
        "EditClientFragment", fragment.findNavController()
    )
}

@Module
interface EditClientBindsModule {
    @Binds
    fun provideInteractor(impl: EditClientInteractorImpl): EditClientInteractor
}

@Module
abstract class EditClientPresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(EditClientViewModel::class)
    abstract fun provideViewModel(viewModel: EditClientViewModel): ViewModel
}