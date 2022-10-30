package ru.kalistratov.template.beauty.domain.feature.edituser

import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.entity.UserData
import ru.kalistratov.template.beauty.presentation.entity.ViewListItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserData

interface EditUserInteractor {
    suspend fun getUser(): User?
    suspend fun getSettingData(user: User): List<EditUserData>
    suspend fun getSettingItems(): List<ViewListItem>
    suspend fun updateUser(userData: UserData): Boolean
}