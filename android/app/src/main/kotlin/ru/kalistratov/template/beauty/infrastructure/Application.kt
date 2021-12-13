package ru.kalistratov.template.beauty.infrastructure

import android.app.Application
import ru.kalistratov.template.beauty.domain.di.ApplicationModule
import ru.kalistratov.template.beauty.domain.di.DaggerApplicationComponent
import ru.kalistratov.template.beauty.domain.di.ServiceModule

class Application : Application() {
    val applicationComponent by lazy {
        DaggerApplicationComponent
            .builder()
            .serviceModule(ServiceModule(this))
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}
