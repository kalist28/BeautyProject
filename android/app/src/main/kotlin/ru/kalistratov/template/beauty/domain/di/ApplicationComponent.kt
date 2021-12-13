package ru.kalistratov.template.beauty.domain.di

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.base.AuthBaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseActivity
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.presentation.feature.auth.di.AuthComponent
import ru.kalistratov.template.beauty.presentation.feature.auth.di.AuthModule
import ru.kalistratov.template.beauty.presentation.feature.registration.di.RegistrationComponent
import ru.kalistratov.template.beauty.presentation.feature.registration.di.RegistrationModule

@Singleton
@Component(
    modules = [
        ServiceModule::class,
        ApplicationModule::class,
        PresentationModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(activity: BaseActivity)
    fun inject(activity: BaseFragment)
    fun inject(activity: AuthBaseFragment)

    fun userComponentBuilder(): UserComponent.Builder

    fun plus(module: RegistrationModule): RegistrationComponent
    fun plus(module: AuthModule): AuthComponent
}

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application
}
