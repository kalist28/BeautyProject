package ru.kalistratov.template.beauty.presentation.feature.edituser

import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItemData
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType
import ru.kalistratov.template.beauty.presentation.feature.edituser.service.EditUserListService

interface EditUserInteractor {
    suspend fun getSettingData(): List<EditUserItemData>
    suspend fun getSettingItems(): List<EditUserItem>
}

class EditUserInteractorImpl(
    private val userRepository: UserRepository,
    private val editUserListService: EditUserListService,
) : EditUserInteractor {

    override suspend fun getSettingData(): List<EditUserItemData> {
        val user = userRepository.get() ?: return emptyList()
        return mutableListOf(
            EditUserItemData(
                EditUserListItemType.EMAIL,
                user.email
            ),
            EditUserItemData(
                EditUserListItemType.NAME,
                user.name
            ),
            EditUserItemData(
                EditUserListItemType.LASTNAME,
                user.surname
            )
        )
    }

    override suspend fun getSettingItems(): List<EditUserItem> =
        editUserListService.getItems()
}
