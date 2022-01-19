package ru.kalistratov.template.beauty.presentation.feature.personalarea

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaFragmentDirections

interface PersonalAreaRouter {
    fun openProfile()
    fun openCalendar()
    fun openTimetable()
}

class PersonalAreaRouterImpl(private val navController: NavController) : PersonalAreaRouter {
    override fun openTimetable() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToTimetableFragment()
    )

    override fun openProfile() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.navigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToCalendarFragment()
    )
}
