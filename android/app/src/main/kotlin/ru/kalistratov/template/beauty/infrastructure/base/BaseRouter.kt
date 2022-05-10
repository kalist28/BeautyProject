package ru.kalistratov.template.beauty.infrastructure.base

import androidx.navigation.NavController
import androidx.navigation.NavDirections

abstract class BaseRouter(val fragment: String) {
    fun NavController.safetyNavigate(
        direction: NavDirections
    ) {
        this.currentDestination?.let {
            if (it.label == fragment) navigate(direction)
        }
    }
}
