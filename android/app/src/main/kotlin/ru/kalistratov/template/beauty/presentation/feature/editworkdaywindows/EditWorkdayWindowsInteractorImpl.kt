package ru.kalistratov.template.beauty.presentation.feature.editworkdaywindows

import ru.kalistratov.template.beauty.common.NetworkResult
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.domain.entity.WorkdaySequence
import ru.kalistratov.template.beauty.domain.entity.WorkdayWindow
import ru.kalistratov.template.beauty.domain.feature.editworkdaywindows.EditWorkdayWindowsInteractor
import ru.kalistratov.template.beauty.domain.repository.api.ApiRepository
import ru.kalistratov.template.beauty.infrastructure.extensions.loge

class EditWorkdayWindowsInteractorImpl(
    private val apiRepository: ApiRepository,
) : EditWorkdayWindowsInteractor {

    override suspend fun getWindows(): List<WorkdayWindow> =
        when (val result = apiRepository.getWindows()) {
            is NetworkResult.Success -> result.value
            is NetworkResult.GenericError -> {
                loge(result)
                emptyList()
            }
        }

    override suspend fun createWindow(
        workdayWindow: WorkdayWindow
    ) = apiRepository.createWorkdayWindow(workdayWindow)

    override suspend fun getWorkdaySequence(daySequenceId: Id): WorkdaySequence =
        when (val result = apiRepository.loadWorkdaySequence(daySequenceId)) {
            is NetworkResult.Success -> result.value
            is NetworkResult.GenericError -> {
                loge(result)
                WorkdaySequence()
            }
        }
}
