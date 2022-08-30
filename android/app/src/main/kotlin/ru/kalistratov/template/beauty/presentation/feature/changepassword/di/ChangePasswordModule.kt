package ru.kalistratov.template.beauty.presentation.feature.changepassword.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.changepassword.ChangePasswordInteractor
import ru.kalistratov.template.beauty.presentation.feature.changepassword.ChangePasswordInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.changepassword.ChangePasswordRouter
import ru.kalistratov.template.beauty.presentation.feature.changepassword.ChangePasswordRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.changepassword.ChangePasswordViewModel
import ru.kalistratov.template.beauty.presentation.feature.changepassword.view.ChangePasswordFragment

@Subcomponent(
    modules = [
        ChangePasswordModule::class,
        ChangePasswordPresentationModule::class,
    ]
)
interface ChangePasswordComponent {
    fun inject(fragment: ChangePasswordFragment)
}

@Module
class ChangePasswordModule(private val fragment: ChangePasswordFragment) {

    @Provides
    fun provideInteractor(): ChangePasswordInteractor =
        ChangePasswordInteractorImpl()

    @Provides
    fun provideRouter(): ChangePasswordRouter =
        ChangePasswordRouterImpl(fragment.findNavController())
}

@Module
abstract class ChangePasswordPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ChangePasswordViewModel::class)
    abstract fun bindViewModel(viewModel: ChangePasswordViewModel): ViewModel
}