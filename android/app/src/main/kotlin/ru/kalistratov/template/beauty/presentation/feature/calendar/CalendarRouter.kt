package ru.kalistratov.template.beauty.presentation.feature.calendar

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.calendar.view.CalendarFragmentDirections

interface CalendarRouter {
    fun openProfile()
    fun openTimetable()
    fun openPersonalArea()
}

class CalendarRouterImpl(
    private val navController: NavController,
    fragment: String
) : BaseRouter(fragment), CalendarRouter {
    override fun openProfile() = navController.safetyNavigate(
        CalendarFragmentDirections.actionCalendarFragmentToProfileFragment()
    )

    override fun openTimetable() = navController.safetyNavigate(
        CalendarFragmentDirections.actionCalendarFragmentToReservationListFragment()
    )

    override fun openPersonalArea() = navController.safetyNavigate(
        CalendarFragmentDirections.actionCalendarFragmentToPersonalAreaFragment()
    )
}
