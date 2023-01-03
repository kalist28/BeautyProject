package ru.kalistratov.template.beauty.infrastructure

import android.app.Application
import android.content.Context
import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.di.ApplicationModule
import ru.kalistratov.template.beauty.infrastructure.di.DaggerApplicationComponent
import ru.kalistratov.template.beauty.infrastructure.di.ServiceModule
import timber.log.Timber

class Application : Application() {

    val applicationComponent by lazy {
        DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    var navController: NavController? = null

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

typealias ApplicationContext = Context
