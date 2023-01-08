package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Date

data class Reservation(
    val id: Id = "",
    val date: Date,
    val offerItem: OfferItem,
    val window: SequenceDayWindow,
)
