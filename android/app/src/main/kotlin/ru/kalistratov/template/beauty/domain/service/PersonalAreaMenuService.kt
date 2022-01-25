package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.presentation.entity.MenuItem

interface PersonalAreaMenuService {
    suspend fun getMenuItems(): List<MenuItem>
}
