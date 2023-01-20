package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentReservationListBinding
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.extensions.getWeekPageTitle
import ru.kalistratov.template.beauty.infrastructure.extensions.toWeekDay
import ru.kalistratov.template.beauty.presentation.extension.clicks
import ru.kalistratov.template.beauty.presentation.extension.connect
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.ReservationListRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.ReservationListState
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.ReservationListViewModel
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.di.ReservationListModule
import java.time.LocalDate
import javax.inject.Inject

sealed interface ReservationListIntent : BaseIntent {
    data class DaySelected(val date: LocalDate) : ReservationListIntent

    object CreateReservation : ReservationListIntent
    object InitData : ReservationListIntent
}

class ReservationListFragment : BaseFragment(),
    BaseView<ReservationListIntent, ReservationListState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var reservationListRouter: ReservationListRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ReservationListViewModel::class.java]
    }

    private val binding: FragmentReservationListBinding by viewBinding(CreateMethod.INFLATE)

    private val reservationListCalendarHelper = ReservationListCalendarHelper()
    private val reservationsController = ReservationsController()

    override fun findViews() {
        find<BottomNavigationView>(R.id.bottom_nav_view).apply {
            selectedItemId = R.id.menu_timetable
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_personal_area -> reservationListRouter.toPersonalArea()
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(ReservationListModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reservationListCalendarHelper.initialize(binding.calendar)
        binding.calendar.weekScrollListener = { weekDays ->
            binding.toolbar.title = getWeekPageTitle(requireContext(), weekDays)
        }

        binding.recycler.connect(reservationsController)

        with(viewModel) {
            router = reservationListRouter
            connectDialogLoadingDisplay()
            connectInto(this@ReservationListFragment)
        }
    }

    override fun intents(): Flow<ReservationListIntent> = merge(
        flowOf(ReservationListIntent.InitData),
        reservationListCalendarHelper.dayClicks().map(ReservationListIntent::DaySelected),
        binding.floatingButton.clicks().map { ReservationListIntent.CreateReservation }
    )

    override fun render(state: ReservationListState) {
        renderDayInfo(state.selectedSequenceDay, state.selectedDay, state.sequenceWeek != null)
        reservationListCalendarHelper.apply {
            sequenceWeek = state.sequenceWeek
            render(state.selectedDay)
        }

        reservationsController.apply {
            reservations = state.filledReservations
            requestModelBuild()
        }
    }

    private fun renderDayInfo(sequenceDay: SequenceDay?, date: LocalDate, isVisible: Boolean) {
        binding.dayTitle.isVisible = isVisible
        binding.daySubtitle.isVisible = isVisible
        binding.dayTitle.text = getString(date.toWeekDay().tittleResId)
        binding.daySubtitle.text = if (sequenceDay != null) getString(
            R.string.workday_arg,
            sequenceDay.timeRange.toString()
        ) else getString(R.string.not_configured)
    }
}
