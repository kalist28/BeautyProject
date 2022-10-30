package ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.domain.repository.SequenceDayWindowsRepository
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsRouter
import ru.kalistratov.template.beauty.domain.service.api.ApiSequenceDayWindowsService
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditSequenceDayWindowsInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.EditWorkdayWindowsViewModel
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.view.EditSequenceDayWindowsFragment

@Subcomponent(
    modules = [
        EditSequenceDayWindowsModule::class,
        EditSequenceDayWindowsPresentationModule::class
    ]
)
interface EditSequenceDayWindowsComponent {
    fun inject(fragment: EditSequenceDayWindowsFragment)
}

@Module
class EditSequenceDayWindowsModule(val fragment: EditSequenceDayWindowsFragment) {

    @Provides
    fun provideEditSequenceDayWindowsInteractor(
        sequenceDayRepository: SequenceDayRepository,
        sequenceDayWindowsRepository: SequenceDayWindowsRepository
    ): EditWorkdayWindowsInteractor = EditSequenceDayWindowsInteractorImpl(
        sequenceDayRepository,
        sequenceDayWindowsRepository
    )

    @Provides
    fun provideEditSequenceDayWindowsRouter(): EditWorkdayWindowsRouter =
        EditWorkdayWindowsRouterImpl(fragment.findNavController())
}

@Module
abstract class EditSequenceDayWindowsPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditWorkdayWindowsViewModel::class)
    abstract fun bindViewModel(viewModel: EditWorkdayWindowsViewModel): ViewModel
}
