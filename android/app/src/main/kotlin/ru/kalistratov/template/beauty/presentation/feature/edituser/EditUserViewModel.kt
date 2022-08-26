package ru.kalistratov.template.beauty.presentation.feature.edituser

import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItemData
import ru.kalistratov.template.beauty.presentation.feature.edituser.view.EditUserIntent

data class EditUserState(
    val settingData: List<EditUserItemData> = emptyList(),
    val settingItems: List<EditUserItem> = emptyList(),
) : BaseState

sealed interface EditUserAction : BaseAction {
    data class UpdateSettingData(val data: List<EditUserItemData>) : EditUserAction
    data class UpdateSettingItems(val items: List<EditUserItem>) : EditUserAction
}

class EditUserViewModel @Inject constructor(
    private val router: EditUserRouter,
    private val interactor: EditUserInteractor,
) : BaseViewModel<EditUserIntent, EditUserAction, EditUserState>() {

    private val initialState = EditUserState()

    private val _stateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val initFlow = intentFlow.filterIsInstance<EditUserIntent.InitData>()
                .share(this)

            val updateSettingItemAction = initFlow
                .map { EditUserAction.UpdateSettingItems(interactor.getSettingItems()) }


            val updateSettingDataAction = initFlow
                .map { EditUserAction.UpdateSettingData(interactor.getSettingData()) }

            merge(
                updateSettingItemAction,
                updateSettingDataAction
            )
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach { _stateFlow.value = it }
                .collect(stateFlow)
        }.addTo(workComposite)
    }

    override fun reduce(
        state: EditUserState,
        action: EditUserAction
    ): EditUserState = when (action) {
        is EditUserAction.UpdateSettingData -> state.copy(
            settingData = action.data
        )
        is EditUserAction.UpdateSettingItems -> state.copy(
            settingItems = action.items
        )
    }
}
