package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.kalistratov.template.beauty.domain.Application
import ru.kalistratov.template.beauty.infrastructure.coroutines.CompositeJob

abstract class AuthBaseFragment : Fragment() {

    protected val appComponent by lazy {
        (activity?.applicationContext as Application).applicationComponent
    }

    protected val jobComposite = CompositeJob()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews()
        injectAppComponent()
    }

    abstract fun injectAppComponent()

    override fun onDestroy() {
        jobComposite.cancel()
        super.onDestroy()
    }

    open fun findViews() = Unit
}
