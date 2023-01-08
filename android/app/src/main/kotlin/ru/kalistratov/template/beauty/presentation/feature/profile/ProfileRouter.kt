package ru.kalistratov.template.beauty.presentation.feature.profile

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.profile.view.ProfileFragmentDirections

interface ProfileRouter {
    fun openCalendar()
    fun openTimetable()
    fun openPersonalArea()
}

class ProfileRouterImpl(
    private val navController: NavController,
    fragment: String
) : BaseRouter(fragment), ProfileRouter {
    override fun openTimetable() = navController.safetyNavigate(
        ProfileFragmentDirections.actionProfileFragmentToReservationListFragment()
    )

    override fun openPersonalArea() = navController.safetyNavigate(
        ProfileFragmentDirections.actionProfileFragmentToPersonalAreaFragment()
    )

    override fun openCalendar() = navController.safetyNavigate(
        ProfileFragmentDirections.actionProfileFragmentToCalendarFragment()
    )
}
