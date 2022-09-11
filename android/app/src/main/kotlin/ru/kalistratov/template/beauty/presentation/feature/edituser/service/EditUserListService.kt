package ru.kalistratov.template.beauty.presentation.feature.edituser.service

import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType

interface EditUserListService {
    suspend fun getItems(): List<EditUserListItem>
}

class EditUserListServiceImpl : EditUserListService {

    private val items = mutableListOf(
        EditUserListItem.EditText(
            R.string.name,
            EditUserListItemType.NAME
        ),
        EditUserListItem.EditText(
            R.string.lastname,
            EditUserListItemType.SURNAME
        ),
        EditUserListItem.EditText(
            R.string.patronymic,
            EditUserListItemType.PATRONYMIC
        ),
        EditUserListItem.EditText(
            R.string.email,
            EditUserListItemType.EMAIL
        ),
        EditUserListItem.Button(
            R.string.update_password,
            EditUserListItemType.CHANGE_PASSWORD_BUTTON
        ),
        EditUserListItem.Button(
            R.string.save,
            EditUserListItemType.SAVE_BUTTON
        ),
    )

    override suspend fun getItems(): List<EditUserListItem> = items
}
