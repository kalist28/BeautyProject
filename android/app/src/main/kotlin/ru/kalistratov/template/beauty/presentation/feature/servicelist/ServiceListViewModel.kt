package ru.kalistratov.template.beauty.presentation.feature.servicelist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.domain.entity.Service
import ru.kalistratov.template.beauty.domain.feature.servicelist.ServiceListInteractor
import ru.kalistratov.template.beauty.infrastructure.base.*
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.presentation.feature.servicelist.view.ServiceListIntent
import javax.inject.Inject

data class ServiceListState(
    val groups: List<Group<Service>> = emptyList()
) : BaseState

sealed interface ServiceListAction : BaseAction {
    data class UpdateServices(val groups: List<Group<Service>>) : ServiceListAction
}

class ServiceListViewModel @Inject constructor(
    private val router: ServiceListRouter,
    private val interactor: ServiceListInteractor
) : BaseViewModel<ServiceListIntent, ServiceListAction, ServiceListState>() {

    private val initialState = ServiceListState()
    private val _stateFlow = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {

            val a = flowOf(
                ServiceListAction.UpdateServices(
                    mutableListOf(
                        Group(
                            1,
                            "Ногти",
                            mutableListOf()
                        ),
                        Group(
                            3,
                            "Волосы =)",
                            mutableListOf()
                        ),
                        Group(
                            2,
                            "Брови",
                            mutableListOf(
                                GroupItem(
                                    Service(
                                        22,
                                        "Брови1"
                                    )
                                ),
                                GroupItem(
                                    Service(
                                        22,
                                        "Брови2"
                                    )
                                ),
                                GroupItem(
                                    Service(
                                        23,
                                        "Брови3"
                                    )
                                ),
                            )
                        )
                    )
                )
            )

            val b = intentFlow.map {
                ServiceListAction.UpdateServices(
                    mutableListOf(
                        Group(
                            1,
                            "Ногти",
                            mutableListOf(
                                GroupItem(
                                    Service(
                                        12,
                                        "Ногти1"
                                    )
                                ),
                                GroupItem(
                                    Service(
                                        12,
                                        "Ногти2"
                                    )
                                ),
                                GroupItem(
                                    Service(
                                        13,
                                        "Ногти3"
                                    )
                                ),
                            )
                        ),
                        Group(
                            3,
                            "Волосы =)",
                            mutableListOf()
                        ),
                        Group(
                            2,
                            "Брови",
                            mutableListOf(
                                GroupItem(
                                    Service(
                                        22,
                                        "Брови1"
                                    )
                                ),
                                GroupItem(
                                    Service(
                                        22,
                                        "Брови2"
                                    )
                                ),
                                GroupItem(
                                    Service(
                                        23,
                                        "Брови3"
                                    )
                                ),
                            )
                        )
                    )
                )
            }

            intentFlow.filterIsInstance<ServiceListIntent.BackPressed>()
                .onEach { router.back() }
                .launchHere()

            merge(a, b)
                .flowOn(Dispatchers.IO)
                .scan(initialState, ::reduce)
                .onEach {
                    stateFlow.emit(it)
                    _stateFlow.value = it
                }
                .launchHere()
        }.addTo(workComposite)
    }

    override fun reduce(
        state: ServiceListState,
        action: ServiceListAction
    ): ServiceListState = when (action) {
        is ServiceListAction.UpdateServices -> state.copy(
            groups = action.groups
        )
    }
}