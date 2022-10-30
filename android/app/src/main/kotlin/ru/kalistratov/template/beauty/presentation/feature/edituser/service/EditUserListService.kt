package ru.kalistratov.template.beauty.presentation.feature.edituser.service

import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.presentation.entity.ViewListItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType

interface EditUserListService {
    suspend fun getItems(): List<ViewListItem>
}

class EditUserListServiceImpl : EditUserListService {

    private val items = mutableListOf(
        ViewListItem.EditText(
            R.string.name,
            EditUserListItemType.Name
        ),
        ViewListItem.EditText(
            R.string.lastname,
            EditUserListItemType.Surname
        ),
        ViewListItem.EditText(
            R.string.patronymic,
            EditUserListItemType.Patronymic
        ),
        ViewListItem.EditText(
            R.string.email,
            EditUserListItemType.Email
        ),
        ViewListItem.Button(
            R.string.update_password,
            EditUserListItemType.ChangePasswordButton
        ),
        ViewListItem.Button(
            R.string.save,
            EditUserListItemType.SaveButton
        ),
    )

    override suspend fun getItems(): List<ViewListItem> = items
}
