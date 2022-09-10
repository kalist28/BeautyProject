package ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.SequenceDay
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.domain.repository.api.ApiWorkdayWindowRepository

class EditWorkdayWindowsInteractorImpl(
    private val apiWorkdayWindowRepository: ApiWorkdayWindowRepository,
) : EditWorkdayWindowsInteractor {

    override suspend fun getWindows(): List<WorkdayWindow> =
        when (val result = apiWorkdayWindowRepository.getAll()) {
            is NetworkResult.Success -> result.value
            is NetworkResult.GenericError -> emptyList()
        }

    override suspend fun createWindow(
        window: WorkdayWindow
    ) = apiWorkdayWindowRepository.create(window)

    override suspend fun updateWindow(
        window: WorkdayWindow
    ) = apiWorkdayWindowRepository.update(window)

    override suspend fun getSequenceDay(dayNumber: Int): SequenceDay =
        when (val result = apiWorkdayWindowRepository.get(dayNumber)) {
            is NetworkResult.Success -> result.value
            is NetworkResult.GenericError -> SequenceDay()
        }
}
