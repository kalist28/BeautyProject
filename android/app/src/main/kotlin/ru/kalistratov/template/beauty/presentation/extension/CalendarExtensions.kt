package ru.kalistratov.template.beauty.presentation.extension

import com.kizitonwose.calendarview.model.CalendarDay
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.presentation.view.SimpleCalendarView

fun SimpleCalendarView.clicks(): Flow<CalendarDay> = callbackFlow {
    onDayClickAction = { trySend(it) }
    awaitClose { onDayClickAction = null }
}.conflate()