package ru.kalistratov.template.beauty.presentation.feature.timetable.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.timetable.TimetableInteractor
import ru.kalistratov.template.beauty.presentation.feature.timetable.TimetableInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.timetable.TimetableViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.view.TimetableFragment

@Subcomponent(
    modules = [
        TimetableModule::class,
        TimetablePresentationModule::class
    ]
)
interface TimetableSubcomponent {
    fun inject(fragment: TimetableFragment)
}

@Module
class TimetableModule(private val fragment: TimetableFragment) {

    @Provides
    fun provideTimetableInteractor(): TimetableInteractor = TimetableInteractorImpl()
}

@Module
abstract class TimetablePresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(TimetableViewModel::class)
    abstract fun bindViewModel(viewModel: TimetableViewModel): ViewModel
}
