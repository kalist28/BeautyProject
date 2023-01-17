package ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.view

import com.airbnb.epoxy.EpoxyController
import ru.kalistratov.template.beauty.domain.entity.Reservation
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.epoxy.ReservationModel

class ReservationsController : EpoxyController() {

    var reservations = emptyList<Reservation>()

    override fun buildModels() = reservations.forEach {
        loge("*** - it")
        ReservationModel(it, marginsBundle = MarginsBundle.base)
            .addTo(this)
    }
}