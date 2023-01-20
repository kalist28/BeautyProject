package ru.kalistratov.template.beauty.presentation.entity

import androidx.annotation.DrawableRes

sealed interface MenuItem {
    data class Container(
        val id: Int,
        @DrawableRes val iconId: Int,
        val title: String
    ) : MenuItem

    object Indent : MenuItem
}
