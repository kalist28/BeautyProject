package ru.kalistratov.template.beauty.presentation.feature.personalarea

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaFragmentDirections

interface PersonalAreaRouter {
    fun openProfile()
    fun openCalendar()
    fun openTimetable()
    fun openWeekSequence()
    fun exit()
}

class PersonalAreaRouterImpl(
    private val navController: NavController,
    fragment: String
) : BaseRouter(fragment), PersonalAreaRouter {
    override fun openProfile() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToProfileFragment()
    )

    override fun openCalendar() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToCalendarFragment()
    )

    override fun openTimetable() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToTimetableFragment()
    )

    override fun openWeekSequence() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToWeekSequenceFragment()
    )

    override fun exit() = navController.safetyNavigate(
        PersonalAreaFragmentDirections.actionPersonalAreaFragmentToAuthFragment()
    )
}
