package ru.kalistratov.template.beauty.infrastructure

import android.app.Application
import androidx.navigation.NavController
import ru.kalistratov.template.beauty.domain.di.ApplicationModule
import ru.kalistratov.template.beauty.domain.di.DaggerApplicationComponent
import ru.kalistratov.template.beauty.domain.di.ServiceModule
import timber.log.Timber

class Application : Application() {

    val applicationComponent by lazy {
        DaggerApplicationComponent
            .builder()
            .serviceModule(ServiceModule(this))
            .applicationModule(ApplicationModule(this))
            .build()
    }

    var navController: NavController? = null

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
