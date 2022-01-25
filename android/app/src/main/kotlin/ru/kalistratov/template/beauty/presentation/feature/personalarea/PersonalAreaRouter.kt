package ru.kalistratov.template.beauty.presentation.feature.personalarea

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaFragmentDirections

interface PersonalAreaRouter {
    fun openProfile()
    fun openCalendar()
    fun openTimetable()
    fun openWeekSequence()
    fun exit()
}

class PersonalAreaRouterImpl(private val navController: NavController) : PersonalAreaRouter {
    override fun openProfile() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToCalendarFragment()
    )

    override fun openTimetable() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToTimetableFragment()
    )

    override fun openWeekSequence() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToWeekSequenceFragment()
    )

    override fun exit() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToAuthFragment()
    )
}
