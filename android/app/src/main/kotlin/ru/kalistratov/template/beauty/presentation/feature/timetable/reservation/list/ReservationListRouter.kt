package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.SafetyRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.view.ReservationListFragmentDirections

interface ReservationListRouter {
    fun toEdit()
    fun openProfile()
    fun openCalendar()
    fun toPersonalArea()
}

class ReservationListRouterImpl(
    private val navController: NavController,
    override val fragmentName: String
) : SafetyRouter(), ReservationListRouter {
    override fun toEdit() = navController.safetyNavigate(
        ReservationListFragmentDirections.actionReservationListFragmentToEditReservationFragment()
    )

    override fun openProfile() = navController.safetyNavigate(
        ReservationListFragmentDirections.actionReservationListFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.safetyNavigate(
        ReservationListFragmentDirections.actionReservationListFragmentToCalendarFragment()
    )

    override fun toPersonalArea() = navController.safetyNavigate(
        ReservationListFragmentDirections.actionReservationListFragmentToPersonalAreaFragment()
    )
}
