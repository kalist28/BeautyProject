package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
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
import ru.kalistratov.template.beauty.presentation.extension.showLoading

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
        setSoftInputMode()
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

    protected fun setAppBar(@StringRes id: Int) = setAppBar(requireContext().getString(id))

    protected fun setAppBar(
        title: String
    ) = find<MaterialToolbar>(R.id.toolbar).apply {
        toolbar = this
        updateAppBarTitle(title)

        (requireActivity() as AppCompatActivity).also {
            it.setSupportActionBar(this)
            appBarMenu()?.let {
                setHasOptionsMenu(true)
                invalidateMenu()
            }
        }
        setOnMenuItemClickListener { onAppBarMenuItemClick(it) }
        setNavigationOnClickListener {
            _backPressedFlow.tryEmit(Unit)
            onAppBarBackPressed()
        }
    }

    protected fun updateAppBarTitle(@StringRes id: Int) {
        updateAppBarTitle(requireContext().getString(id))
    }

    protected fun updateAppBarTitle(title: String) {
        toolbar?.title = title
    }

    protected fun setSoftInputMode() {
        requireActivity().window.setSoftInputMode(getSoftInputMode())
    }

    protected open fun getSoftInputMode() =
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE


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

    fun ViewModel.connectDialogLoadingDisplay() {
        if (this !is ViewModelLoadingSupport) error("Notification flow is not impl.")
        viewModelScope.launch {
            loadingUpdates().collect(::showLoading)
        }.addTo(jobComposite)
    }

    fun ViewModel.connectNotifications() {
        if (this !is ViewModelNotificationSupport) error("Notification flow is not impl.")
        viewModelScope.launch {
            notifications().collect(::showNotification)
        }.addTo(jobComposite)
    }

    protected fun showNotification(notification: ViewNotification) = when (notification) {
        is ViewNotification.Toast -> Toast.makeText(
            requireContext(),
            notification.message,
            if (notification.showLong) Toast.LENGTH_LONG
            else Toast.LENGTH_SHORT
        ).show()
    }
}