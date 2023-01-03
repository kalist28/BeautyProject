package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.infrastructure.di.UserComponent

interface SessionManager {
    fun getComponent(): UserComponent
    fun closeSession()
}
