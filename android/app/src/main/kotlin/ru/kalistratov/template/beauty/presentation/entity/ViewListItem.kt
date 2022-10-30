package ru.kalistratov.template.beauty.presentation.entity

import androidx.annotation.StringRes

interface ViewListItemType

sealed class ViewListItem(open val type: ViewListItemType) {
    data class EditText(
        @StringRes val titleId: Int,
        override val type: ViewListItemType
    ) : ViewListItem(type)

    data class Button(
        @StringRes val titleId: Int,
        override val type: ViewListItemType
    ) : ViewListItem(type)
}