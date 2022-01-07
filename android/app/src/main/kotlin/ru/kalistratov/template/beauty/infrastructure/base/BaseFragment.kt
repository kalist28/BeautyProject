package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob
import javax.inject.Inject

interface BaseView<I : BaseIntent, S : BaseState> {
    fun intents(): Flow<I>
    fun render(state: S)
}

abstract class BaseFragment : Fragment() {

    private val appComponent by lazy {
        (activity?.applicationContext as Application).applicationComponent
    }

    @Inject
    lateinit var sessionManager: SessionManager

    protected val jobComposite = CompositeJob()

    protected var toolbar: Toolbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectAppComponent()
        //toolbar = activity?.findViewById(R.id.toolbar) as? Toolbar?
        setSupportToolbar(toolbar)
        findViews()
    }

    protected open fun injectAppComponent() {
        appComponent.inject(this)
        val userComponent = sessionManager.getComponent()
        injectUserComponent(userComponent)
    }

    override fun onDestroy() {
        jobComposite.cancel()
        super.onDestroy()
    }

    private fun setSupportToolbar(toolbar: Toolbar?) =
        (activity as AppCompatActivity?)
            ?.setSupportActionBar(toolbar)

    open fun injectUserComponent(userComponent: UserComponent) = Unit

    open fun findViews() = Unit
}
