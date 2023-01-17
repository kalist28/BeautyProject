package ru.kalistratov.template.beauty.infrastructure.helper.mapper

import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import ru.kalistratov.template.beauty.common.DateTimeFormat.DATE_STANDART
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.Reservation
import ru.kalistratov.template.beauty.interfaces.server.dto.MakeReservationRequest
import ru.kalistratov.template.beauty.interfaces.server.dto.ServerReservation

fun Reservation.toMakeRequest() = MakeReservationRequest(
    dateAt = date.format(DATE_STANDART),
    itemId = item.id,
    windowId = window.id,
    createdBy = client.id
)

fun ServerReservation.toLocal() = Reservation(
    id = id,
    date = DateFormat(DATE_STANDART).parse(dateAt).local.date,
    item = item.data.toLocal(),
    window = window.data.toLocal(),
    client = Client.EMPTY
)