package ru.kalistratov.template.beauty.domain.feature.personalarea

import ru.kalistratov.template.beauty.presentation.entity.MenuItem

interface PersonalAreaInteractor {
    suspend fun loadMenuItems(): List<MenuItem>
    suspend fun exit()
}
