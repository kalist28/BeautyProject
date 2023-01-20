package ru.kalistratov.template.beauty.presentation.view

import android.view.View
import android.view.ViewGroup
import ru.kalistratov.template.beauty.presentation.extension.dpToPx

data class MarginsBundle(
    val startMarginDp: Int? = null,
    val topMarginDp: Int? = null,
    val endMarginDp: Int? = null,
    val bottomMarginDp: Int? = null,
) {
    companion object {
        val baseHorizontal = MarginsBundle(
            startMarginDp = Margins.BASE_HORIZONTAL,
            endMarginDp = Margins.BASE_HORIZONTAL
        )

        val smallVertical = MarginsBundle(
            topMarginDp = Margins.SMALL_VERTICAL,
            bottomMarginDp = Margins.SMALL_VERTICAL
        )

        val baseVertical = MarginsBundle(
            topMarginDp = Margins.BASE_VERTICAL,
            bottomMarginDp = Margins.BASE_VERTICAL
        )

        val base = MarginsBundle(
            startMarginDp = Margins.BASE_HORIZONTAL,
            topMarginDp = Margins.BASE_VERTICAL,
            endMarginDp = Margins.BASE_HORIZONTAL,
            bottomMarginDp = Margins.BASE_VERTICAL
        )

        fun horizontalOf(dp: Int) = MarginsBundle(
            startMarginDp = dp,
            endMarginDp = dp
        )
    }
}

object Margins {
    const val SMALL_VERTICAL = 2
    const val BASE_VERTICAL = 8
    const val BASE_HORIZONTAL = 16
    const val SMALL_HORIZONTAL = 8
}

fun View.setMargins(
    leftMarginDp: Int? = null,
    topMarginDp: Int? = null,
    rightMarginDp: Int? = null,
    bottomMarginDp: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        leftMarginDp?.run { params.leftMargin = this.dpToPx(context) }
        topMarginDp?.run { params.topMargin = this.dpToPx(context) }
        rightMarginDp?.run { params.rightMargin = this.dpToPx(context) }
        bottomMarginDp?.run { params.bottomMargin = this.dpToPx(context) }
        requestLayout()
    }
}

fun View.setMargins(bundle: MarginsBundle) = with(bundle) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        endMarginDp?.run { params.rightMargin = this.dpToPx(context) }
        startMarginDp?.run { params.leftMargin = this.dpToPx(context) }
        topMarginDp?.run { params.topMargin = this.dpToPx(context) }
        bottomMarginDp?.run { params.bottomMargin = this.dpToPx(context) }
        requestLayout()
    }
}

fun View.setBaseMargin() {
    setVerticalMargin()
    setHorizontalMargin()
}

fun View.setVerticalMargin(
    dp: Int = Margins.BASE_VERTICAL
) = setMargins(
    topMarginDp = dp,
    bottomMarginDp = dp
)

fun View.setHorizontalMargin(
    dp: Int = Margins.BASE_HORIZONTAL
) = setMargins(
    leftMarginDp = dp,
    rightMarginDp = dp
)