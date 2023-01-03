package ru.kalistratov.template.beauty.infrastructure.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import ru.kalistratov.template.beauty.infrastructure.Application
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
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    abstract fun injectAppComponent()

    override fun onDestroy() {
        jobComposite.cancel()
        super.onDestroy()
    }

    open fun findViews() = Unit
}
