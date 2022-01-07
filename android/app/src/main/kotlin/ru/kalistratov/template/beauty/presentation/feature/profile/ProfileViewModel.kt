package ru.kalistratov.template.beauty.presentation.feature.profile

import ru.kalistratov.template.beauty.infrastructure.base.BaseAction
import ru.kalistratov.template.beauty.infrastructure.base.BaseState
import ru.kalistratov.template.beauty.infrastructure.base.BaseViewModel
import ru.kalistratov.template.beauty.presentation.feature.profile.view.ProfileIntent
import javax.inject.Inject

data class ProfileState(
    val a: Int = 0
) : BaseState

sealed class ProfileAction : BaseAction

class ProfileViewModel @Inject constructor() :
    BaseViewModel<ProfileIntent, ProfileAction, ProfileState>() {

    override fun reduce(state: ProfileState, action: ProfileAction): ProfileState {
        TODO("Not yet implemented")
    }
}
