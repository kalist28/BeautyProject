package ru.kalistratov.template.beauty.presentation.feature.auth.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.auth.AuthInteractor
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.service.AuthService
import ru.kalistratov.template.beauty.presentation.feature.auth.AuthInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.auth.AuthRouter
import ru.kalistratov.template.beauty.presentation.feature.auth.AuthRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.auth.AuthViewModel
import ru.kalistratov.template.beauty.presentation.feature.auth.view.AuthFragment

@Subcomponent(
    modules = [
        AuthModule::class,
        AuthPresentationModule::class,
    ]
)
interface AuthComponent {
    fun inject(module: AuthFragment)
}

@Module
class AuthModule(private val fragment: AuthFragment) {

    @Provides
    fun provideAuthInteractor(
        authService: AuthService,
        userRepository: UserRepository
    ): AuthInteractor = AuthInteractorImpl(
        authService,
        userRepository
    )

    @Provides
    fun provideAuthRouter(): AuthRouter = AuthRouterImpl(
        fragment.findNavController(),
        fragment.javaClass.simpleName
    )
}

@Module
abstract class AuthPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindViewModel(viewModel: AuthViewModel): ViewModel
}
