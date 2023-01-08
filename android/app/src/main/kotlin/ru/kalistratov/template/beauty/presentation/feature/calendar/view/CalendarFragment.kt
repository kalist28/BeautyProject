package ru.kalistratov.template.beauty.presentation.feature.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.databinding.FragmentCalendarBinding
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.extension.onCloses
import ru.kalistratov.template.beauty.presentation.extension.showBottomSheet
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarRouter
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarState
import ru.kalistratov.template.beauty.presentation.feature.calendar.CalendarViewModel
import ru.kalistratov.template.beauty.presentation.feature.calendar.di.CalendarModule
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.DayDetailsBottomSheet
import java.time.LocalDate
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

    private var selectedDate = LocalDate.now()
    private val binding: FragmentCalendarBinding by viewBinding(CreateMethod.INFLATE)

    private val dayDetailsBottomSheet by lazy { DayDetailsBottomSheet() }


    override fun findViews() {
        /*find<BottomNavigationView>(R.id.bottom_nav_view).apply {
            selectedItemId = R.id.menu_calendar
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_profile -> calendarRouter.openProfile()
                    R.id.menu_timetable -> calendarRouter.openTimetable()
                    R.id.menu_personal_area -> calendarRouter.openPersonalArea()
                }
                return@setOnItemSelectedListener true
            }
        }*/
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(CalendarModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

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
     //   calendarView.clicks().map { CalendarIntent.DaySelected(it) },
        dayDetailsBottomSheet.onCloses().map { CalendarIntent.SelectedDayCloses }
    )

    override fun render(state: CalendarState) {
        if (state.showDayDetails) showBottomSheet(dayDetailsBottomSheet)
    }
}
