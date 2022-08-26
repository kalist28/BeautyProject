package ru.kalistratov.template.beauty.presentation.feature.edituser.entity

import androidx.annotation.StringRes

enum class EditUserListItemType {
    NAME,
    LASTNAME,

    // USER_LOGIN,
    GENDER,
    EMAIL
}

sealed interface EditUserItem {
    data class EditText(
        @StringRes val titleId: Int,
        val type: EditUserListItemType
    ) : EditUserItem

    data class Button(
        @StringRes val resTitle: Int,
    ) : EditUserItem
}

data class EditUserItemData(
    val type: EditUserListItemType,
    val value: String
)
