package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.UserModule
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.SessionManager

class SessionManagerImpl(
    private val application: Application,
    private val authSettingsService: AuthSettingsService,
) : SessionManager {

    private var component: UserComponent? = null

    override fun getComponent(): UserComponent =
        component ?: createComponent()

    override fun clearSession() {
        component = null
    }

    private fun createComponent(): UserComponent {
        val userName = authSettingsService.getUserId() ?: throw RuntimeException("User not found.")
        val component = application.applicationComponent
            .userComponentBuilder()
            .sessionModule(UserModule(userName))
            .build()
        this.component = component
        return component
    }
}
