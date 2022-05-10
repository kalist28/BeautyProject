package ru.kalistratov.template.beauty.presentation.feature.timetable

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.view.TimetableFragmentDirections

interface TimetableRouter {
    fun openProfile()
    fun openCalendar()
    fun toPersonalArea()
}

class TimetableRouterImpl(
    private val navController: NavController,
    fragment: String
) : BaseRouter(fragment), TimetableRouter {
    override fun openProfile() = navController.safetyNavigate(
        TimetableFragmentDirections.actionTimetableFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.safetyNavigate(
        TimetableFragmentDirections.actionTimetableFragmentToCalendarFragment()
    )

    override fun toPersonalArea() = navController.safetyNavigate(
        TimetableFragmentDirections.actionTimetableFragmentToPersonalAreaFragment()
    )
}
