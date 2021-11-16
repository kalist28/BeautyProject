package ru.kalistratov.template.beauty.domain.service

import ru.kalistratov.template.beauty.domain.Application
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.UserModule

interface SessionManager {
    fun getComponent(): UserComponent
    fun clearSession()
}

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
        val userName = authSettingsService.getUser() ?: throw RuntimeException("User not found.")
        val component = application.applicationComponent
            .userComponentBuilder()
            .sessionModule(UserModule(userName))
            .build()
        this.component = component
        return component
    }
}
