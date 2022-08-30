package ru.kalistratov.template.beauty.presentation.feature.edituser.entity

import androidx.annotation.StringRes

enum class EditUserListItemType {
    NAME,
    LASTNAME,

    // USER_LOGIN,
    GENDER,
    EMAIL,

    SAVE_BUTTON,
    CHANGE_PASSWORD_BUTTON
}

sealed class EditUserItem(open val type: EditUserListItemType) {
    data class EditText(
        @StringRes val titleId: Int,
        override val type: EditUserListItemType
    ) : EditUserItem(type)

    data class Button(
        @StringRes val titleId: Int,
        override val type: EditUserListItemType
    ) : EditUserItem(type)
}

data class EditUserItemData(
    val type: EditUserListItemType,
    val value: String
)
