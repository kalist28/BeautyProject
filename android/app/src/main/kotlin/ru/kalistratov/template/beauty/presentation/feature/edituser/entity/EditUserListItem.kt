package ru.kalistratov.template.beauty.presentation.feature.edituser.entity

import ru.kalistratov.template.beauty.presentation.entity.ViewListItemType

sealed interface EditUserListItemType : ViewListItemType {
    object Name : EditUserListItemType
    object Surname : EditUserListItemType
    object Patronymic : EditUserListItemType
    object Gender : EditUserListItemType

    object Email : EditUserListItemType
    object UserLogin : EditUserListItemType

    object SaveButton : EditUserListItemType
    object ChangePasswordButton: EditUserListItemType
}

data class EditUserData(
    val type: EditUserListItemType,
    val value: String
)
