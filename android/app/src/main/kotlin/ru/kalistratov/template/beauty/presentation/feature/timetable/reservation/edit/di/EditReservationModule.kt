package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.timetable.reservation.edit.EditReservationInteractor
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.EditReservationViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view.EditReservationFragment

@Subcomponent(
    modules = [
        EditReservationModule::class,
        EditReservationPresentationModule::class,
    ]
)
interface EditReservationComponent {
    fun inject(fragment: EditReservationFragment)
}

@Module
class EditReservationModule(
    private val fragment: EditReservationFragment
) {

    @Provides
    fun provideRouter(): EditReservationRouter = EditReservationRouterImpl(
        "EditReservationFragment", fragment.findNavController()
    )
}

@Module
interface EditReservationPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditReservationViewModel::class)
    fun provideViewModel(vm: EditReservationViewModel): ViewModel

    @Binds
    fun provideInteractor(impl: EditReservationInteractorImpl): EditReservationInteractor
}