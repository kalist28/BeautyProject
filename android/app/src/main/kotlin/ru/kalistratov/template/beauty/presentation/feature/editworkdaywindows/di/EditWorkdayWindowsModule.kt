package ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.EditWorkdayWindowsInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.EditWorkdayWindowsViewModel
import ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows.view.EditWorkdayWindowsFragment

@Subcomponent(
    modules = [
        EditWorkdayWindowsModule::class,
        EditWorkdayWindowsPresentationModule::class
    ]
)
interface EditWorkdayWindowsComponent {
    fun inject(fragment: EditWorkdayWindowsFragment)
}

@Module
class EditWorkdayWindowsModule(val fragment: EditWorkdayWindowsFragment) {

    @Provides
    fun provideEditWorkdayWindowsInteractor(
        apiRepository: ApiRepository
    ): EditWorkdayWindowsInteractor = EditWorkdayWindowsInteractorImpl(
        apiRepository
    )
}

@Module
abstract class EditWorkdayWindowsPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditWorkdayWindowsViewModel::class)
    abstract fun bindViewModel(viewModel: EditWorkdayWindowsViewModel): ViewModel
}
