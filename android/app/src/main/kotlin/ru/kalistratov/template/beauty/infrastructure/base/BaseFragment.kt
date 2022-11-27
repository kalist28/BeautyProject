package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
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

    protected var toolbar: MaterialToolbar? = null

    private val _backPressedFlow = mutableSharedFlow<Unit>()
    protected val backPressedFlow = _backPressedFlow.asSharedFlow()

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

    protected fun setAppBar(
        title: String
    ) = find<MaterialToolbar>(R.id.toolbar).apply {
        toolbar = this
        updateAppBarTitle(title)

        (requireActivity() as AppCompatActivity).setSupportActionBar(this)

        setOnMenuItemClickListener { onAppBarMenuItemClick(it) }
        setNavigationOnClickListener {
            _backPressedFlow.tryEmit(Unit)
            onAppBarBackPressed()
        }
    }

    protected fun updateAppBarTitle(title: String) {
        toolbar?.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        appBarMenu()?.let { inflater.inflate(it, menu) }
    }

    open fun injectUserComponent(userComponent: UserComponent) = Unit

    open fun findViews() = Unit

    open fun initViews() = Unit

    open fun onAppBarBackPressed() = Unit

    open fun onAppBarMenuItemClick(item: MenuItem): Boolean = false

    @MenuRes
    open fun appBarMenu(): Int? = null

    @Deprecated("setToolbar")
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