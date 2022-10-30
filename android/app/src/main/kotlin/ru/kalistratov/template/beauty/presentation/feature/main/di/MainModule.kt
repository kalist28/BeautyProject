package ru.kalistratov.template.beauty.presentation.feature.main.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.service.MainInteractor
import ru.kalistratov.template.beauty.domain.service.PermissionsService
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.main.MainInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.main.MainViewModel
import ru.kalistratov.template.beauty.presentation.feature.main.view.MainActivity

@Subcomponent(
    modules = [
        MainModule::class,
        MainPresentationModule::class,
    ]
)
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}

@Module
class MainModule() {

    @Provides
    fun provideMainInteractor(
        permissionsService: PermissionsService
    ): MainInteractor = MainInteractorImpl(
        permissionsService
    )
}

@Module
abstract class MainPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindViewModel(viewModel: MainViewModel): ViewModel
}
