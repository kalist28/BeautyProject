package ru.kalistratov.template.beauty.presentation.feature.personalarea.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.personalarea.PersonalAreaInteractor
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.PersonalAreaMenuService
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaRouter
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.personalarea.PersonalAreaViewModel
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaFragment

@Subcomponent(
    modules = [
        PersonalAreaModule::class,
        PersonalAreaPresentationModule::class,
    ]
)
interface PersonalAreaComponent {
    fun inject(fragment: PersonalAreaFragment)
}

@Module
class PersonalAreaModule(private val fragment: PersonalAreaFragment) {

    @Provides
    fun provideProfileInteractor(
        userRepository: UserRepository,
        authSettingsService: AuthSettingsService,
        personalAreaMenuService: PersonalAreaMenuService
    ): PersonalAreaInteractor = PersonalAreaInteractorImpl(
        userRepository,
        personalAreaMenuService,
        authSettingsService
    )

    @Provides
    fun provideProfileRouter(): PersonalAreaRouter = PersonalAreaRouterImpl(
        fragment.findNavController(),
        fragment.javaClass.simpleName
    )
}

@Module
abstract class PersonalAreaPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(PersonalAreaViewModel::class)
    abstract fun bindViewModel(viewModel: PersonalAreaViewModel): ViewModel
}
