package ru.kalistratov.template.beauty.presentation.feature.edituser.entity

import androidx.annotation.StringRes

enum class EditUserListItemType {
    NAME,
    SURNAME,
    PATRONYMIC,
    GENDER,

    EMAIL,
    USER_LOGIN,

    SAVE_BUTTON,
    CHANGE_PASSWORD_BUTTON
}

sealed class EditUserListItem(open val type: EditUserListItemType) {
    data class EditText(
        @StringRes val titleId: Int,
        override val type: EditUserListItemType
    ) : EditUserListItem(type)

    data class Button(
        @StringRes val titleId: Int,
        override val type: EditUserListItemType
    ) : EditUserListItem(type)
}

data class EditUserData(
    val type: EditUserListItemType,
    val value: String
)
