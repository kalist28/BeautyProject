package ru.kalistratov.template.beauty.presentation.feature.calendar

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.calendar.view.CalendarFragmentDirections

interface CalendarRouter {
    fun openProfile()
    fun openTimetable()
}

class CalendarRouterImpl(private val navController: NavController) : CalendarRouter {
    override fun openProfile() = navController.navigate(
        CalendarFragmentDirections.actionCalendarFragmentToProfileFragment()
    )

    override fun openTimetable() = navController.navigate(
        CalendarFragmentDirections.actionCalendarFragmentToTimetableFragment()
    )
}
