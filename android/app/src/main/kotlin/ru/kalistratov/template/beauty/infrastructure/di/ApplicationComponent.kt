package ru.kalistratov.template.beauty.infrastructure.di

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.kalistratov.template.beauty.BuildConfig
import ru.kalistratov.template.beauty.domain.entity.ServerUrl
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.interfaces.server.service.ApiUserService
import ru.kalistratov.template.beauty.domain.service.RegistrationStepService
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.ApplicationContext
import ru.kalistratov.template.beauty.infrastructure.base.AuthBaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseActivity
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.repository.UserRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.service.RegistrationStepServiceImpl
import ru.kalistratov.template.beauty.presentation.feature.auth.di.AuthComponent
import ru.kalistratov.template.beauty.presentation.feature.auth.di.AuthModule
import ru.kalistratov.template.beauty.presentation.feature.main.di.MainComponent
import ru.kalistratov.template.beauty.presentation.feature.main.di.MainModule
import ru.kalistratov.template.beauty.presentation.feature.registration.di.RegistrationComponent
import ru.kalistratov.template.beauty.presentation.feature.registration.di.RegistrationModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        ServiceModule::class,
        RepositoryModule::class,
        ApplicationModule::class,
        PresentationModule::class,
    ]
)
interface ApplicationComponent {
    fun inject(activity: BaseActivity)
    fun inject(activity: BaseFragment)
    fun inject(activity: AuthBaseFragment)

    fun userComponentBuilder(): UserComponent.Builder

    fun plus(module: MainModule): MainComponent
    fun plus(module: RegistrationModule): RegistrationComponent
    fun plus(module: AuthModule): AuthComponent

    fun getRegistrationStepService(): RegistrationStepService
}

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun provideApplicationContext(): ApplicationContext = application

    @Provides
    @Singleton
    fun provideServerUrl(): ServerUrl = BuildConfig.SERVER_URI

    @Provides
    @Singleton
    fun provideRegistrationStepService(): RegistrationStepService = RegistrationStepServiceImpl()

    @Provides
    @Singleton
    fun provideUserRepository(
        apiUserService: ApiUserService
    ): UserRepository = UserRepositoryImpl(
        apiUserService
    )
}
