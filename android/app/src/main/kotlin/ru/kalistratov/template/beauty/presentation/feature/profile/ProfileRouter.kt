package ru.kalistratov.template.beauty.presentation.feature.profile

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.profile.view.ProfileFragmentDirections

interface ProfileRouter {
    fun openCalendar()
    fun openTimetable()
    fun openPersonalArea()
}

class ProfileRouterImpl(private val navController: NavController) : ProfileRouter {
    override fun openTimetable() = navController.navigate(
        ProfileFragmentDirections.actionProfileFragmentToTimetableFragment()
    )

    override fun openPersonalArea() = navController.navigate(
        ProfileFragmentDirections.actionProfileFragmentToPersonalAreaFragment()
    )

    override fun openCalendar() = navController.navigate(
        ProfileFragmentDirections.actionProfileFragmentToCalendarFragment()
    )
}
