package ru.kalistratov.template.beauty.presentation.feature.registration.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.registration.RegistrationInteractor
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationRouter
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.registration.RegistrationViewModel
import ru.kalistratov.template.beauty.presentation.feature.registration.view.RegistrationFragment

@Subcomponent(
    modules = [
        RegistrationModule::class,
        RegistrationPresentationModule::class
    ]
)
interface RegistrationComponent {
    fun inject(fragment: RegistrationFragment)
}

@Module
class RegistrationModule(val fragment: RegistrationFragment) {

    @Provides
    fun provideRegistrationInteractor(
        authService: AuthService
    ): RegistrationInteractor = RegistrationInteractorImpl(
        authService
    )

    @Provides
    fun provideRegistrationRouter(): RegistrationRouter = RegistrationRouterImpl(
        fragment.findNavController(),
        fragment.javaClass.simpleName
    )
}

@Module
abstract class RegistrationPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindViewModel(viewModel: RegistrationViewModel): ViewModel
}
