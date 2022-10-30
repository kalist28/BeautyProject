package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.service.PersonalAreaMenuService
import ru.kalistratov.template.beauty.presentation.entity.MenuItem
import ru.kalistratov.template.beauty.presentation.feature.personalarea.entity.PersonalAreaMenuItemId

class PersonalAreaMenuServiceImpl : PersonalAreaMenuService {
    override suspend fun getMenuItems(): List<MenuItem> = listOf(
        MenuItem(
            PersonalAreaMenuItemId.WEEK_SEQUENCE.id,
            R.drawable.ic_week_sequence,
            "Рабочая неделя"
        ),
        MenuItem(
            PersonalAreaMenuItemId.SERVICES.id,
            R.drawable.ic_list_alt,
            "Услуги"
        ),
        MenuItem(
            PersonalAreaMenuItemId.CLIENTS.id,
            R.drawable.ic_clients,
            "Клиенты"
        ),
        MenuItem(
            PersonalAreaMenuItemId.EXIT.id,
            R.drawable.ic_exit,
            "Выход"
        )
    )
}
