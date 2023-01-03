package ru.kalistratov.template.beauty.presentation.feature.contactpicker.di

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import ru.kalistratov.template.beauty.domain.feature.contactpicker.ContactPickerInteractor
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelKey
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.ContactPickerInteractorImpl
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.ContactPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.ContactPickerRouterImpl
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.ContactPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.view.ContactPickerFragment

@Subcomponent(
    modules = [
        ContactPickerModule::class,
        ContactPickerBindsModule::class,
        ContactPickerPresentationModule::class,
    ]
)
interface ContactPickerComponent {
    fun inject(fragment: ContactPickerFragment)
}

@Module
class ContactPickerModule(private val fragment: ContactPickerFragment) {

    @Provides
    fun provideRouter(): ContactPickerRouter = ContactPickerRouterImpl(
        fragment.javaClass.simpleName,
        fragment.findNavController()
    )
}

@Module
interface ContactPickerBindsModule {

    @Binds
    fun provideInteractor(impl: ContactPickerInteractorImpl): ContactPickerInteractor
}

@Module
abstract class ContactPickerPresentationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ContactPickerViewModel::class)
    abstract fun bindViewModel(viewModel: ContactPickerViewModel): ViewModel
}
