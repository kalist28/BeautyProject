package ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.offer.my.picker.MyOfferPickerInteractor
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.MyOfferPickerInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.MyOfferPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.MyOfferPickerRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.MyOfferPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.view.MyOfferPickerFragment

@Subcomponent(
    modules = [
        MyOfferPickerModule::class,
        MyOfferPickerBindsModule::class,
    ]
)
interface MyOfferPickerComponent {
    fun inject(fragment: MyOfferPickerFragment)
}

@Module
class MyOfferPickerModule(val fragment: MyOfferPickerFragment) {
    @Provides
    fun provideMyOfferPickerRouter(): MyOfferPickerRouter = MyOfferPickerRouterImpl(
        "MyOfferPickerFragment", fragment.findNavController()
    )
}

@Module
interface MyOfferPickerBindsModule {

    @Binds
    fun bindsMyOfferPickerInteractor(impl: MyOfferPickerInteractorImpl): MyOfferPickerInteractor

    @Binds
    @IntoMap
    @ViewModelKey(MyOfferPickerViewModel::class)
    fun bindsViewModel(vm: MyOfferPickerViewModel): ViewModel
}