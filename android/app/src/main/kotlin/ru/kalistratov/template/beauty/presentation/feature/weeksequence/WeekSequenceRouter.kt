package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaFragmentDirections
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.view.WeekSequenceFragmentDirections

interface WeekSequenceRouter {
    fun openProfile()
    fun openCalendar()
    fun openTimetable()
    fun openEditWorkdayWindows(dayNumber: Int)
    fun back()
}

class WeekSequenceRouterImpl(
    private val navController: NavController,
    fragment: String
) : BaseRouter(fragment), WeekSequenceRouter {

    override fun openProfile() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToCalendarFragment()
    )

    override fun openTimetable() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToTimetableFragment()
    )

    override fun openEditWorkdayWindows(dayNumber: Int) = navController.safetyNavigate(
        WeekSequenceFragmentDirections.actionWeekSequenceFragmentToEditWorkdayWindowsFragment(
            dayNumber
        )
    )

    override fun back() {
        navController.popBackStack()
    }
}
