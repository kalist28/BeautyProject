package ru.kalistratov.template.beauty.infrastructure.base

import androidx.navigation.NavController
import androidx.navigation.NavDirections

@Deprecated("Use SafetyRouter")
abstract class BaseRouter(
    private val fragmentName: String
) {
    fun NavController.safetyNavigate(
        direction: NavDirections
    ) {
        this.currentDestination?.let {
            if (it.label == fragmentName)
                navigate(direction)
        }
    }
}

abstract class SafetyRouter {
    abstract val fragmentName: String
    fun NavController.safetyNavigate(
        direction: NavDirections
    ) {
        this.currentDestination?.let {
            if (it.label == fragmentName)
                navigate(direction)
        }
    }
}
