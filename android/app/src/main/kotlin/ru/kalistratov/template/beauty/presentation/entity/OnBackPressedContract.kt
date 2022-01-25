package ru.kalistratov.template.beauty.presentation.entity

// TODO Think about it in the future
interface OnBackPressListener {
    fun OnBackPressed()
}

data class OnBackPressCallback(var listener: OnBackPressListener? = null)
