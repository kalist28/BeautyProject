package ru.kalistratov.template.beauty.presentation.feature.myofferlist

import androidx.navigation.NavController
import ru.kalistratov.template.beauty.infrastructure.base.BaseRouter
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.view.MyOfferListFragmentDirections
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.entity.OfferPickerType

interface MyOfferListRouter {
    fun back()
    fun openOfferPicker(type: OfferPickerType)
}

class MyOfferListRouterImpl(
    fragmentName: String,
    private val navController: NavController
): BaseRouter(fragmentName), MyOfferListRouter {
    override fun back() {
        navController.popBackStack()
    }

    override fun openOfferPicker(type: OfferPickerType) = navController.safetyNavigate(
        MyOfferListFragmentDirections.actionMyOfferListFragmentToOfferPickerFragment(
            type.title,
            if (type is OfferPickerType.Type) type.id else null
        )
    )
}