package ru.kalistratov.template.beauty.presentation.extension

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.presentation.entity.OnBackPressedCallbackWrapper
import ru.kalistratov.template.beauty.presentation.feature.main.view.MainActivity

fun TextView.textChanges(): Flow<CharSequence> = callbackFlow {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable) = Unit
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            trySend(s)
        }
    }

    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
}.conflate()

fun View.clicks(): Flow<Unit> = callbackFlow {
    kotlinx.coroutines.delay(200)
    setOnClickListener { trySend(Unit) }
    awaitClose { setOnClickListener(null) }
}.conflate()

fun onBackPressClicks(callback: OnBackPressedCallbackWrapper): Flow<Unit> = callbackFlow {
    callback.callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            trySend(Unit)
        }
    }
    awaitClose { callback.callback = null }
}.conflate()

fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(ContextCompat.getColor(context, color))

fun View.getDrawable(@DrawableRes id: Int): Drawable? =
    AppCompatResources.getDrawable(this.context, id)

fun View.getColorStateList(@ColorRes id: Int): ColorStateList =
    AppCompatResources.getColorStateList(this.context, id)

fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Activity.showLoading(show: Boolean) {
    if (this is MainActivity) {
        loadingDialog.show(show)
    }
}

fun Fragment.showLoading(show: Boolean) {
    requireActivity().showLoading(show)
}