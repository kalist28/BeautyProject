package ru.kalistratov.template.beauty.presentation.feature.timetable.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.feature.timetable.TimetableRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.TimetableState
import ru.kalistratov.template.beauty.presentation.feature.timetable.TimetableViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.di.TimetableModule

sealed class TimetableIntent : BaseIntent

class TimetableFragment : BaseFragment(), BaseView<TimetableIntent, TimetableState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var timetableRouter: TimetableRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[TimetableViewModel::class.java]
    }

    override fun findViews() {

        find<BottomNavigationView>(R.id.bottom_nav_view).apply {
            selectedItemId = R.id.menu_timetable
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_profile -> timetableRouter.openProfile()
                    R.id.menu_calendar -> timetableRouter.openCalendar()
                    R.id.menu_personal_area -> timetableRouter.toPersonalArea()
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(TimetableModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_timetable, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun intents(): Flow<TimetableIntent> = emptyFlow()

    override fun render(state: TimetableState) {
    }
}
