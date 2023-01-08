package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.list.ReservationListInteractor
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.ReservationListInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.ReservationListRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.ReservationListRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.ReservationListViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.view.ReservationListFragment

@Subcomponent(
    modules = [
        ReservationListModule::class,
        ReservationListPresentationModule::class
    ]
)
interface ReservationListComponent {
    fun inject(fragment: ReservationListFragment)
}

@Module
class ReservationListModule(private val fragment: ReservationListFragment) {
    @Provides
    fun provideTimetableRouter(): ReservationListRouter = ReservationListRouterImpl(
        fragment.findNavController(),
        "ReservationListFragment"
    )
}

@Module
interface ReservationListPresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReservationListViewModel::class)
    fun bindViewModel(viewModel: ReservationListViewModel): ViewModel

    @Binds
    fun provideTimetableInteractor(impl: ReservationListInteractorImpl): ReservationListInteractor
}
