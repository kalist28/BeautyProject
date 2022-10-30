package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.domain.entity.SequenceDayWindow

interface SequenceService {
    suspend fun updateWindow(window: SequenceDayWindow)
}