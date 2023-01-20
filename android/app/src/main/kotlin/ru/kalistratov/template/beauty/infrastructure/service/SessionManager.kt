package ru.kalistratov.template.beauty.infrastructure.service

import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.UserModule
import ru.kalistratov.template.beauty.domain.service.AuthSettingsService
import ru.kalistratov.template.beauty.domain.service.SessionManager
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import javax.inject.Inject

class SessionManagerImpl @Inject constructor(
    private val application: Application,
    private val authSettingsService: AuthSettingsService,
) : SessionManager {

    private var component: UserComponent? = null

    override fun getComponent(): UserComponent =
        component ?: createComponent()

    override fun closeSession() {
        component = null
        authSettingsService.exit()
        application.activity?.run {
            updateNavGraph()
            loadingDialog?.show(false)
        }
    }

    private fun createComponent(): UserComponent {
        val userName = authSettingsService.getUserId() ?: "".also { closeSession() }
        val component = application.applicationComponent
            .userComponentBuilder()
            .sessionModule(UserModule(userName))
            .build()
        this.component = component
        return component
    }
}
