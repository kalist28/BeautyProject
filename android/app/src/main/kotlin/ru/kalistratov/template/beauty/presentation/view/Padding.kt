package ru.kalistratov.template.beauty.presentation.view

import android.view.View
import androidx.core.view.updatePadding
import ru.kalistratov.template.beauty.presentation.extension.dpToPx

data class PaddingBundle(
    val startMarginDp: Int? = null,
    val topMarginDp: Int? = null,
    val endMarginDp: Int? = null,
    val bottomMarginDp: Int? = null,
) {
    companion object {
        val baseHorizontal = PaddingBundle(
            startMarginDp = Padding.BASE_HORIZONTAL,
            endMarginDp = Padding.BASE_HORIZONTAL
        )

        val baseVertical = PaddingBundle(
            topMarginDp = Padding.BASE_VERTICAL,
            bottomMarginDp = Padding.BASE_VERTICAL
        )

        val base = PaddingBundle(
            startMarginDp = Padding.BASE_HORIZONTAL,
            topMarginDp = Padding.BASE_VERTICAL,
            endMarginDp = Padding.BASE_HORIZONTAL,
            bottomMarginDp = Padding.BASE_VERTICAL
        )
    }
}

object Padding {
    const val BASE_VERTICAL = 8
    const val BASE_HORIZONTAL = 16
    const val SMALL_HORIZONTAL = 8
}

fun View.updatePadding(
    leftMarginDp: Int? = null,
    topMarginDp: Int? = null,
    rightMarginDp: Int? = null,
    bottomMarginDp: Int? = null
) = updatePadding(
    leftMarginDp?.dpToPx(context) ?: paddingEnd,
    topMarginDp?.dpToPx(context) ?: paddingTop,
    rightMarginDp?.dpToPx(context) ?: paddingStart,
    bottomMarginDp?.dpToPx(context) ?: paddingBottom,
)

fun View.updatePadding(bundle: PaddingBundle) = with(bundle) {
    updatePadding(
        endMarginDp,
        startMarginDp,
        topMarginDp,
        bottomMarginDp,
    )
}