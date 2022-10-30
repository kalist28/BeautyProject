package ru.kalistratov.template.beauty.presentation.feature.calendar.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.calendar.CalendarInteractor
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarRouter
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarViewModel
import ru.kalistratov.template.beauty.presentation.feature.calendar.view.CalendarFragment

@Subcomponent(
    modules = [
        CalendarModule::class,
        CalendarPresentationModule::class
    ]
)
interface CalendarComponent {
    fun inject(fragment: CalendarFragment)
}

@Module
class CalendarModule(private val fragment: CalendarFragment) {

    @Provides
    fun provideCalendarInteractor(): CalendarInteractor = CalendarInteractorImpl()

    @Provides
    fun provideCalendarRouter(): CalendarRouter =
        CalendarRouterImpl(
            fragment.findNavController(),
            fragment.javaClass.simpleName
        )
}

@Module
abstract class CalendarPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(CalendarViewModel::class)
    abstract fun bindViewModel(viewModel: CalendarViewModel): ViewModel
}
