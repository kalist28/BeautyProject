package ru.kalistratov.template.beauty.presentation.extension

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.presentation.entity.OnBackPressCallback
import ru.kalistratov.template.beauty.presentation.entity.OnBackPressListener

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

fun onBackPressClicks(callback: OnBackPressCallback): Flow<Unit> = callbackFlow {
    callback.listener = object : OnBackPressListener {
        override fun OnBackPressed() { trySend(Unit) }
    }
    awaitClose { }
}.conflate()

fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(ContextCompat.getColor(context, color))

fun View.getDrawable(@DrawableRes id: Int): Drawable? =
    AppCompatResources.getDrawable(this.context, id)
