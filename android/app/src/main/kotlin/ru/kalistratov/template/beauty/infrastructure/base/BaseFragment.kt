package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.find

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
        initViews()
    }

    protected open fun injectAppComponent() {
        appComponent.inject(this)
        val userComponent = sessionManager.getComponent()
        injectUserComponent(userComponent)
    }

    override fun onDestroyView() {
        jobComposite.cancel()
        super.onDestroyView()
    }

    open fun injectUserComponent(userComponent: UserComponent) = Unit

    open fun findViews() = Unit

    open fun initViews() = Unit

    open fun setTitle(title: String) {
        find<TextView>(R.id.topic_text_view).text = title
    }

    fun <I : BaseIntent, A : BaseAction, S : BaseState>
            BaseViewModel<I, A, S>.connectInto(view: BaseView<I, S>) {
        with(this) {
            viewModelScope.launch(Dispatchers.Main) {
                stateUpdates().collect(view::render)
            }.addTo(jobComposite)
            processIntent(view.intents())
        }
    }
}