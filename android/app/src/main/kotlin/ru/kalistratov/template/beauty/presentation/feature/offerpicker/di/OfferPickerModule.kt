package ru.kalistratov.template.beauty.presentation.feature.offerpicker.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.OfferPickerInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.OfferPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.OfferPickerRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.OfferPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.view.OfferPickerFragment
import ru.kalistratov.template.beauty.domain.feature.servicelist.OfferPickerInteractor
import ru.kalistratov.template.beauty.domain.repository.OfferCategoryRepository
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey

@Subcomponent(
    modules = [
        OfferPickerModule::class,
        OfferPickerPresentationModule::class
    ]
)
interface OfferPickerComponent {
    fun inject(fragment: OfferPickerFragment)
}

@Module
class OfferPickerModule(private val fragment: OfferPickerFragment) {

    @Provides
    fun provideInteractor(
        offerCategoryRepository: OfferCategoryRepository
    ): OfferPickerInteractor = OfferPickerInteractorImpl(
        offerCategoryRepository
    )

    @Provides
    fun provideRouter(): OfferPickerRouter =
        OfferPickerRouterImpl(fragment.findNavController())
}

@Module
abstract class OfferPickerPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(OfferPickerViewModel::class)
    abstract fun bindViewModel(viewModel: OfferPickerViewModel): ViewModel
}