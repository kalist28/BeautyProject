package ru.kalistratov.template.beauty.presentation.feature.personalarea

import ru.kalistratov.template.beauty.domain.entity.User
import ru.kalistratov.template.beauty.domain.feature.personalarea.PersonalAreaInteractor
import ru.kalistratov.template.beauty.domain.repository.UserRepository
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.PersonalAreaMenuService
import ru.kalistratov.template.beauty.presentation.entity.MenuItem

class PersonalAreaInteractorImpl(
    private val userRepository: UserRepository,
    private val personalAreaMenuService: PersonalAreaMenuService,
    private val authSettingsService: AuthSettingsService
) : PersonalAreaInteractor {

    override suspend fun loadUser(): User? = userRepository.get()

    override suspend fun loadMenuItems(): List<MenuItem> =
        personalAreaMenuService.getMenuItems()

    override suspend fun exit() = authSettingsService.exit()
}
