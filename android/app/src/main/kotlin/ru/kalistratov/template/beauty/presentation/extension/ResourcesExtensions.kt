package ru.kalistratov.template.beauty.presentation.extension

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

fun Resources.loadDrawable(
    @DrawableRes resId: Int,
    theme: Resources.Theme? = null,
) = ResourcesCompat.getDrawable(this, resId, theme)
