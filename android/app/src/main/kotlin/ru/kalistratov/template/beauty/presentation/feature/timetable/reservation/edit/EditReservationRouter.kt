package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.SafetyRouter
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.view.EditReservationFragmentDirections

interface EditReservationRouter {
    fun back()
    fun toClientPicker()
    fun toMyOfferPicker()
}

class EditReservationRouterImpl(
    override val fragmentName: String,
    private val navController: NavController
) : SafetyRouter(), EditReservationRouter {
    override fun back() {
        navController.popBackStack()
    }

    override fun toClientPicker() = navController.safetyNavigate(
        EditReservationFragmentDirections.actionEditReservationFragmentToClientPickerFragment()
    )

    override fun toMyOfferPicker() = navController.safetyNavigate(
        EditReservationFragmentDirections.actionEditReservationFragmentToMyOfferPickerFragment()
    )
}