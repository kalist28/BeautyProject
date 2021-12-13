package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injectAppComponent()
        findViews()
    }

    private fun injectAppComponent() {
        appComponent.inject(this)
        val userComponent = sessionManager.getComponent()
        injectUserComponent(userComponent)
    }

    override fun onDestroy() {
        jobComposite.cancel()
        super.onDestroy()
    }

    open fun injectUserComponent(userComponent: UserComponent) = Unit

    open fun findViews() = Unit
}
