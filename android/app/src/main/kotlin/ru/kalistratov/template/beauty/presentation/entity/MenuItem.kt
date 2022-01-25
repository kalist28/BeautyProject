package ru.kalistratov.template.beauty.presentation.entity

import androidx.annotation.DrawableRes

data class MenuItem(
    val id: Int,
    @DrawableRes val iconId: Int,
    val title: String
)
