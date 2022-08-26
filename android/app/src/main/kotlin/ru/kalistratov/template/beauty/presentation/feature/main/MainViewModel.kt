package ru.kalistratov.template.beauty.presentation.feature.main

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.feature.main.view.MainIntent
import javax.inject.Inject

data class MainState(
    val allowOnBackPress: Boolean = false
) : BaseState

sealed class MainAction : BaseAction {
    object OnBackPressed : MainAction()
    object CancelBackPressed : MainAction()
}

class MainViewModel @Inject constructor() : BaseViewModel<MainIntent, MainAction, MainState>() {

    companion object {
        private const val WAITING_TIME_TO_EXIT = 1500L
    }

    init {
        viewModelScope.launch {
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
                cancelBackPressedAction
            )
                .flowOn(Dispatchers.IO)
                .scan(MainState(), ::reduce)
                .onEach { stateFlow.emit(it) }
                .launchIn(this)
                .addTo(workComposite)
        }
    }

    override fun reduce(state: MainState, action: MainAction): MainState = when (action) {
        MainAction.CancelBackPressed -> state.copy(allowOnBackPress = false)
        MainAction.OnBackPressed -> state.copy(allowOnBackPress = true)
    }
}
