package ru.kalistratov.template.beauty.presentation.feature.timetable

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.timetable.view.TimetableFragmentDirections

interface TimetableRouter {
    fun openProfile()
    fun openCalendar()
}

class TimetableRouterImpl(private val navController: NavController) : TimetableRouter {
    override fun openProfile() = navController.navigate(
        TimetableFragmentDirections.actionTimetableFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.navigate(
        TimetableFragmentDirections.actionTimetableFragmentToCalendarFragment()
    )
}
