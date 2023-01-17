package ru.kalistratov.template.beauty.domain.entity

import com.soywiz.klock.Date

data class Reservation(
    val id: Id = "",
    val date: Date,
    val client: Client,
    val item: OfferItem,
    val window: SequenceDayWindow
)