package ru.kalistratov.template.beauty.presentation.feature.main

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.service.MainInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.entity.RequestPermission
import ru.kalistratov.template.beauty.presentation.feature.main.view.MainIntent
import javax.inject.Inject

data class MainState(
    val allowOnBackPress: Boolean = false,
    val requestPermission: RequestPermission? = null
) : BaseState

sealed class MainAction : BaseAction {
    data class UpdateRequestPermission(val request: RequestPermission) : MainAction()
    object Clear : MainAction()
    object OnBackPressed : MainAction()
    object CancelBackPressed : MainAction()
}

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor
) : BaseViewModel<MainIntent, MainAction, MainState>() {

    companion object {
        private const val WAITING_TIME_TO_EXIT = 1500L
    }

    init {
        viewModelScope.launch {

            intentFlow.filterIsInstance<MainIntent.RequestPermissionsResultReceived>()
                .onEach { interactor.pushRequestPermissionsResult(it.result) }
                .launchIn(this)

            val updateRequestPermission = interactor.requests()
                .flatMapConcat {
                    flowOf(
                        MainAction.UpdateRequestPermission(it),
                        MainAction.Clear
                    )
                }

            val onBackPressedAction = intentFlow
                .filterIsInstance<MainIntent.OnBackPressed>()
                .map { MainAction.OnBackPressed }

            val cancelBackPressedAction = onBackPressedAction
                .map {
                    delay(WAITING_TIME_TO_EXIT)
                    MainAction.CancelBackPressed
                }

            merge(
                onBackPressedAction,
                cancelBackPressedAction,
                updateRequestPermission
            )
                .flowOn(Dispatchers.IO)
                .scan(MainState(), ::reduce)
                .onEach { stateFlow.emit(it) }
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: MainState, action: MainAction): MainState = when (action) {
        is MainAction.CancelBackPressed -> state.copy(allowOnBackPress = false)
        is MainAction.OnBackPressed -> state.copy(allowOnBackPress = true)
        is MainAction.UpdateRequestPermission -> state.copy(requestPermission = action.request)
        is MainAction.Clear -> state.copy(requestPermission = null)
    }
}
