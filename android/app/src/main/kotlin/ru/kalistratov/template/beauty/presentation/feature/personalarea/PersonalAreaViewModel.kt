package ru.kalistratov.template.beauty.presentation.feature.personalarea

import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.feature.personalarea.PersonalAreaInteractor
import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.coroutines.share
import ru.kalistratov.template.beauty.presentation.entity.MenuItem
import ru.kalistratov.template.beauty.presentation.feature.personalarea.entity.PersonalAreaMenuItemId
import ru.kalistratov.template.beauty.presentation.feature.personalarea.view.PersonalAreaIntent

data class PersonalAreaState(
    val menuItems: List<MenuItem> = emptyList(),
) : BaseState

sealed class PersonalAreaAction : BaseAction {
    data class UpdateMenuItems(val items: List<MenuItem>) : PersonalAreaAction()
}

class PersonalAreaViewModel @Inject constructor(
    private val router: PersonalAreaRouter,
    private val interactor: PersonalAreaInteractor
) : BaseViewModel<PersonalAreaIntent, PersonalAreaAction, PersonalAreaState>() {

    private val initialState = PersonalAreaState()
    private val _stateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val initFlow = intentFlow
                .filterIsInstance<PersonalAreaIntent.InitData>()
                .share(this)

            val updateMenuItemsAction = initFlow.map {
                val items = interactor.loadMenuItems()
                PersonalAreaAction.UpdateMenuItems(items)
            }

            intentFlow.filterIsInstance<PersonalAreaIntent.UserPanelClick>()
                .debounce(300)
                .onEach { router.openEditUser() }
                .launchIn(this)
                .addTo(workComposite)

            intentFlow.filterIsInstance<PersonalAreaIntent.MenuItemClick>()
                .onEach {
                    when (it.id) {
                        PersonalAreaMenuItemId.WEEK_SEQUENCE.id -> router.openWeekSequence()
                        PersonalAreaMenuItemId.SERVICES.id -> Unit
                        PersonalAreaMenuItemId.EXIT.id -> {
                            interactor.exit()
                            router.exit()
                        }
                    }
                }
                .launchIn(this)
                .addTo(workComposite)

            merge(updateMenuItemsAction)
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach { _stateFlow.value = it }
                .collect(stateFlow)
        }.addTo(workComposite)
    }

    override fun reduce(state: PersonalAreaState, action: PersonalAreaAction) = when (action) {
        is PersonalAreaAction.UpdateMenuItems -> state.copy(menuItems = action.items)
    }
}
