package ru.kalistratov.template.beauty.presentation.feature.profile.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.profile.ProfileInteractor
import ru.kalistratov.template.beauty.presentation.feature.profile.ProfileInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.profile.ProfileRouter
import ru.kalistratov.template.beauty.presentation.feature.profile.ProfileRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.profile.ProfileViewModel
import ru.kalistratov.template.beauty.presentation.feature.profile.view.ProfileFragment

@Subcomponent(
    modules = [
        ProfileModule::class,
        ProfilePresentationModule::class,
    ]
)
interface ProfileComponent {
    fun inject(fragment: ProfileFragment)
}

@Module
class ProfileModule(private val fragment: ProfileFragment) {

    @Provides
    fun provideProfileInteractor(): ProfileInteractor = ProfileInteractorImpl()

    @Provides
    fun provideProfileRouter(): ProfileRouter =
        ProfileRouterImpl(fragment.findNavController())
}

@Module
abstract class ProfilePresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindViewModel(viewModel: ProfileViewModel): ViewModel
}
