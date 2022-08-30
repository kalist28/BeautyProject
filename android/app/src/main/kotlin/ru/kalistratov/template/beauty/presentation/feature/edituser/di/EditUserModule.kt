package ru.kalistratov.template.beauty.presentation.feature.edituser.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.domain.repository.api.ApiUserRepository
import ru.kalistratov.template.beauty.infrastructure.repository.UserRepositoryImpl
import ru.kalistratov.template.beauty.presentation.feature.edituser.*
import ru.kalistratov.template.beauty.presentation.feature.edituser.service.EditUserListService
import ru.kalistratov.template.beauty.presentation.feature.edituser.service.EditUserListServiceImpl
import ru.kalistratov.template.beauty.presentation.feature.edituser.view.EditUserFragment

@Subcomponent(
    modules = [
        EditUserModule::class,
        EditUserPresentationModule::class,
    ]
)
interface EditUserComponent {
    fun inject(fragment: EditUserFragment)
}

@Module
class EditUserModule(private val fragment: EditUserFragment) {

    @Provides
    fun provideEditUserInteractor(
        editUserListService: EditUserListService,
        userRepository: UserRepository
    ): EditUserInteractor = EditUserInteractorImpl(
        userRepository,
        editUserListService
    )

    @Provides
    fun provideEditUserRouter(): EditUserRouter = EditUserRouterImpl(
        fragment.findNavController()
    )

    @Provides
    fun provideEditUserListService(): EditUserListService = EditUserListServiceImpl()
}

@Module
abstract class EditUserPresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(EditUserViewModel::class)
    abstract fun bindViewModel(viewModel: EditUserViewModel): ViewModel
}
