package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.domain.di.UserComponent

interface SessionManager {
    fun getComponent(): UserComponent
    fun clearSession()
}
