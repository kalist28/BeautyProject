package ru.kalistratov.template.beauty.presentation.feature.edituser

import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.UserData
import ru.kalistratov.template.beauty.domain.feature.edituser.EditUserInteractor
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.presentation.entity.ViewListItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserData
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType
import ru.kalistratov.template.beauty.presentation.feature.edituser.service.EditUserListService

class EditUserInteractorImpl(
    private val userRepository: UserRepository,
    private val editUserListService: EditUserListService,
) : EditUserInteractor {
    override suspend fun getUser(): User? =
        userRepository.get()

    override suspend fun getSettingData(
        user: User
    ) = mutableListOf(
        EditUserData(
            EditUserListItemType.Email,
            user.email
        ),
        EditUserData(
            EditUserListItemType.Patronymic,
            user.patronymic
        ),
        EditUserData(
            EditUserListItemType.Name,
            user.name
        ),
        EditUserData(
            EditUserListItemType.Surname,
            user.surname
        )
    )


    override suspend fun getSettingItems(): List<ViewListItem> =
        editUserListService.getItems()

    override suspend fun updateUser(userData: UserData): Boolean {
        userRepository.update(userData)
        return false
    }
}
