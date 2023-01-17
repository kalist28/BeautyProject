package ru.kalistratov.template.beauty.presentation.view

import androidx.annotation.DrawableRes

data class TextFieldDrawableBundle(
    @DrawableRes val left: Int? = null,
    @DrawableRes val top: Int? = null,
    @DrawableRes val bottom: Int? = null,
    @DrawableRes val right: Int? = null,
)

