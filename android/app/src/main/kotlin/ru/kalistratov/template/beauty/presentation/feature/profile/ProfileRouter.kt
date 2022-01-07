package ru.kalistratov.template.beauty.presentation.feature.profile

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.presentation.feature.profile.view.ProfileFragmentDirections

interface ProfileRouter {
    fun openTimetable()
    fun openCalendar()
}

class ProfileRouterImpl(private val navController: NavController) : ProfileRouter {
    override fun openTimetable() = navController.navigate(
        ProfileFragmentDirections.actionProfileFragmentToTimetableFragment()
    )

    override fun openCalendar() = navController.navigate(
        ProfileFragmentDirections.actionProfileFragmentToCalendarFragment()
    )
}
