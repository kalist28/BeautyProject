package ru.kalistratov.template.beauty.presentation.feature.edituser.service

import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType

interface EditUserListService {
    suspend fun getItems(): List<EditUserItem>
}

class EditUserListServiceImpl : EditUserListService {
    override suspend fun getItems(): List<EditUserItem> = mutableListOf(
        EditUserItem.EditText(
            R.string.name,
            EditUserListItemType.NAME
        ),
        EditUserItem.EditText(
            R.string.lastname,
            EditUserListItemType.LASTNAME
        ),
        EditUserItem.EditText(
            R.string.email,
            EditUserListItemType.EMAIL
        ),
        EditUserItem.Button(
            R.string.update_password,
            EditUserListItemType.CHANGE_PASSWORD_BUTTON
        ),
        EditUserItem.Button(
            R.string.save,
            EditUserListItemType.SAVE_BUTTON
        ),
    )
}
