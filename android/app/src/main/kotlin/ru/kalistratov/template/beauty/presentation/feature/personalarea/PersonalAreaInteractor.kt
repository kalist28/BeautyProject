package ru.kalistratov.template.beauty.presentation.feature.personalarea

import ru.kalistratov.template.beauty.domain.feature.personalarea.PersonalAreaInteractor
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.PersonalAreaMenuService
import ru.kalistratov.template.beauty.presentation.entity.MenuItem

class PersonalAreaInteractorImpl(
    private val personalAreaMenuService: PersonalAreaMenuService,
    private val authSettingsService: AuthSettingsService
) : PersonalAreaInteractor {

    override suspend fun exit() = authSettingsService.exit()

    override suspend fun loadMenuItems(): List<MenuItem> =
        personalAreaMenuService.getMenuItems()
}
