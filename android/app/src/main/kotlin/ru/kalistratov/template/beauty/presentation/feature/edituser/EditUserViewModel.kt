package ru.kalistratov.template.beauty.presentation.feature.edituser

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.UserData
import ru.kalistratov.template.beauty.domain.feature.edituser.EditUserInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.infrastructure.coroutines.textDebounce
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserData
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType
import ru.kalistratov.template.beauty.presentation.feature.edituser.view.EditUserIntent
import javax.inject.Inject

data class EditUserState(
    val user: User? = null,
    var userData: UserData = UserData(),
    val allowSaveChanges: Boolean = false,
    val settingData: List<EditUserData> = emptyList(),
    val settingItems: List<EditUserListItem> = emptyList(),
) : BaseState

sealed interface EditUserAction : BaseAction {
    data class UpdateUser(val user: User?) : EditUserAction
    data class UpdateUserData(val userData: UserData) : EditUserAction
    data class UpdateAllowSave(val allow: Boolean) : EditUserAction
    data class UpdateSettingData(val data: List<EditUserData>) : EditUserAction
    data class UpdateSettingItems(val items: List<EditUserListItem>) : EditUserAction
}

class EditUserViewModel @Inject constructor(
    private val router: EditUserRouter,
    private val interactor: EditUserInteractor,
) : BaseViewModel<EditUserIntent, EditUserAction, EditUserState>() {

    private val initialState = EditUserState()

    private val _stateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val initFlow = intentFlow
                .filterIsInstance<EditUserIntent.InitData>()
                .take(1)
                .flatMapConcat {
                    interactor.getUser()
                        ?.let { flowOf(it) }
                        ?: emptyFlow()
                }
                .flowOn(Dispatchers.IO)
                .share(this)

            val updateUserAction = initFlow
                .flatMapConcat { user ->
                    flowOf(
                        EditUserAction.UpdateUser(user),
                        EditUserAction.UpdateUserData(
                            UserData(
                                user.name,
                                user.surname,
                                user.patronymic,
                            )
                        ),
                    )
                }

            val userDataUpdateFlow = intentFlow
                .filterIsInstance<EditUserIntent.DataChanges>()
                .textDebounce()
                .map { intent ->
                    val state = _stateFlow.value
                    val userData = state.userData

                    val data = intent.data
                    val value = data.value

                    val changedUserData = when (data.type) {
                        EditUserListItemType.NAME -> userData.copy(name = value)
                        EditUserListItemType.SURNAME -> userData.copy(surname = value)
                        EditUserListItemType.PATRONYMIC -> userData.copy(patronymic = value)
                        else -> userData
                    }

                    changedUserData to state.user
                }
                .share(this)

            val updateUserDataAction = userDataUpdateFlow
                .flatMapConcat {
                    val user = it.second
                    val userData = it.first
                    val contentChanged = userData.contentChanged(user)
                    if (!contentChanged) emptyFlow()
                    else flowOf(EditUserAction.UpdateUserData(userData))
                }

            val updateAllowSaveAction = userDataUpdateFlow
                .map {
                    val user = it.second
                    val userData = it.first
                    val isAllowToSave = userData.equalsChangeableUserContent(user).not()
                    EditUserAction.UpdateAllowSave(isAllowToSave)
                }

            val updateSettingDataAction = initFlow
                .flatMapConcat {
                    flowOf(
                        EditUserAction.UpdateSettingData(interactor.getSettingData(it)),
                        EditUserAction.UpdateSettingItems(interactor.getSettingItems())
                    ).flowOn(Dispatchers.IO)
                }

            intentFlow.filterIsInstance<EditUserIntent.ButtonClick>()
                .onEach {
                    when (it.type) {
                        EditUserListItemType.SAVE_BUTTON -> updateUser()
                        EditUserListItemType.CHANGE_PASSWORD_BUTTON -> router.openChangePassword()
                        else -> Unit
                    }
                }
                .launchHere()

            intentFlow.filterIsInstance<EditUserIntent.BackPressed>()
                .onEach { router.exit() }
                .launchHere()

            merge(
                updateUserAction,
                updateUserDataAction,
                updateAllowSaveAction,
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
        is EditUserAction.UpdateAllowSave -> state.copy(
            allowSaveChanges = action.allow
        )
        is EditUserAction.UpdateUser -> state.copy(
            user = action.user
        )
        is EditUserAction.UpdateUserData -> state.copy(
            userData = action.userData
        )
    }

    private suspend fun updateUser() =
        interactor.updateUser(_stateFlow.value.userData)
}
