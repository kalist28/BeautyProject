package ru.kalistratov.template.beauty.presentation.feature.weeksequence.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.di.ViewModelKey
import ru.kalistratov.template.beauty.domain.feature.weeksequence.WeekSequenceInteractor
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceRouter
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.WeekSequenceViewModel
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.view.WeekSequenceFragment

@Subcomponent(
    modules = [
        WeekSequenceModule::class,
        WeekSequencePresentationModule::class,
    ]
)
interface WeekSequenceComponent {
    fun inject(fragment: WeekSequenceFragment)
}

@Module
class WeekSequenceModule(private val fragment: WeekSequenceFragment) {

    @Provides
    fun provideWeekSequenceInteractor(
        workSequenceService: WorkSequenceService
    ): WeekSequenceInteractor = WeekSequenceInteractorImpl(
        workSequenceService
    )

    @Provides
    fun provideWeekSequenceRouter(): WeekSequenceRouter =
        WeekSequenceRouterImpl(fragment.findNavController())
}

@Module
abstract class WeekSequencePresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(WeekSequenceViewModel::class)
    abstract fun bindViewModel(viewModel: WeekSequenceViewModel): ViewModel
}
