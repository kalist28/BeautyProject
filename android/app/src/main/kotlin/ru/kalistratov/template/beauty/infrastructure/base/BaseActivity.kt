package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    protected val appComponent by lazy { (application as Application).applicationComponent }

    @Inject
    lateinit var authSettingsService: AuthSettingsService

    protected val jobComposite = CompositeJob()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        injectComponent()
    }

    abstract fun injectComponent()

    override fun onDestroy() {
        jobComposite.cancel()
        super.onDestroy()
    }
}
