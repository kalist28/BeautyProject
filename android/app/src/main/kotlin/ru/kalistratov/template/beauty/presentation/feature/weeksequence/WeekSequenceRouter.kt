package ru.kalistratov.template.beauty.presentation.feature.weeksequence

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaFragmentDirections
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.view.WeekSequenceFragmentDirections

interface WeekSequenceRouter {
    fun openProfile()
    fun openCalendar()
    fun openTimetable()
    fun openEditWorkdayWindows(daySequence: Int)
    fun back()
}

class WeekSequenceRouterImpl(private val navController: NavController) : WeekSequenceRouter {

    override fun openProfile() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToCalendarFragment()
    )

    override fun openTimetable() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToTimetableFragment()
    )

    override fun openEditWorkdayWindows(daySequence: Int) = navController.navigate(
        WeekSequenceFragmentDirections.actionWeekSequenceFragmentToEditWorkdayWindowsFragment(
            daySequence
        )
    )

    override fun back() {
        navController.popBackStack()
    }
}
