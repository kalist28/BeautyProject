package ru.kalistratov.template.beauty.presentation.view.epoxy

import com.airbnb.epoxy.DataBindingEpoxyModel
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setBaseMargin
import ru.kalistratov.template.beauty.presentation.view.setMargins

fun DataBindingEpoxyModel.DataBindingHolder.setBaseMargins() {
    dataBinding.root.setBaseMargin()
}

fun DataBindingEpoxyModel.DataBindingHolder.setMargins(
    marginsBundle: MarginsBundle? = null
) {
    marginsBundle?.let(dataBinding.root::setMargins)
}