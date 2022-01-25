package ru.kalistratov.template.beauty.presentation.feature.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kizitonwose.calendarview.model.CalendarDay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.extension.onCloses
import ru.kalistratov.template.beauty.presentation.extension.showBottomSheet
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarRouter
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarState
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarViewModel
import ru.kalistratov.template.beauty.presentation.feature.calendar.di.CalendarModule
import ru.kalistratov.template.beauty.presentation.view.SimpleCalendarView
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.DayDetailsBottomSheet
import java.util.*
import javax.inject.Inject

sealed class CalendarIntent : BaseIntent {
    data class DaySelected(val day: CalendarDay) : CalendarIntent()
    object SelectedDayCloses : CalendarIntent()
}

class CalendarFragment : BaseFragment(), BaseView<CalendarIntent, CalendarState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var calendarRouter: CalendarRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CalendarViewModel::class.java]
    }

    lateinit var calendarView: SimpleCalendarView

    private val dayDetailsBottomSheet by lazy { DayDetailsBottomSheet() }

    override fun findViews() {
        calendarView = find(R.id.simple_calendar_view)

        find<BottomNavigationView>(R.id.bottom_nav_view).apply {
            selectedItemId = R.id.menu_calendar
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_profile -> calendarRouter.openProfile()
                    R.id.menu_timetable -> calendarRouter.openProfile()
                    R.id.menu_personal_area -> calendarRouter.openPersonalArea()
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(CalendarModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_calendar, container, false)

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

    override fun intents(): Flow<CalendarIntent> = merge(
        calendarView.clicks().map { CalendarIntent.DaySelected(it) },
        dayDetailsBottomSheet.onCloses().map { CalendarIntent.SelectedDayCloses }
    )

    override fun render(state: CalendarState) {
        calendarView.selectedDay = state.selectedDay
        if (state.showDayDetails) showBottomSheet(dayDetailsBottomSheet)
    }
}
