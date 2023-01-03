package ru.kalistratov.template.beauty.presentation.view.processor

import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

abstract class TextFieldErrorProcessor {

    enum class Type {
        ACTIVE, PASSIVE
    }

    data class NotBlank(
        override val type: Type,
        @StringRes override val errorId: Int,
    ) : TextFieldErrorProcessor() {
        override fun errorExist(text: String): Boolean = text.isBlank()
    }

    data class Regex(
        override val type: Type,
        @StringRes override val errorId: Int,
        @StringRes val blankErrorId: Int,
        val regex: kotlin.text.Regex,
    ) : TextFieldErrorProcessor() {
        override fun errorExist(text: String) = (text matches regex).not()

        override fun getError(text: String?) = when (text.isNullOrBlank()) {
            true -> blankErrorId
            false -> when (errorExist(text)) {
                true -> errorId
                false -> null
            }
        }
    }

    private val textWatchers = mutableListOf<TextWatcher>()

    abstract val errorId: Int
    abstract val type: Type

    var editText: EditText? = null
    var textInputLayout: TextInputLayout? = null

    var listener: ((Boolean) -> Unit)? = null

    abstract fun errorExist(text: String): Boolean

    fun setup(
        editText: EditText,
        layout: TextInputLayout,
        checkOnSet: Boolean = true
    ) {
        this.editText = editText
        this.textInputLayout = layout

        clearWatchers()
        when (type) {
            Type.PASSIVE -> {
                setAddTextChangedListener { textInputLayout?.error = null }
            }
            Type.ACTIVE -> {
                setAddTextChangedListener(::showError)
                if (checkOnSet) showError()
            }
        }
    }

    fun showError(
        text: String? = editText?.text?.toString()
    ) = getError(text).let { errorId ->
        textInputLayout?.apply { error = errorId?.let(context::getString) }
        listener?.invoke(errorId == null)
        errorId != null
    }

    protected open fun getError(
        text: String? = null
    ): Int? {
        if (text == null) return null
        return when (errorExist(text)) {
            true -> errorId
            false -> null
        }
    }

    private fun setAddTextChangedListener(action: (String?) -> Unit) = editText
        ?.addTextChangedListener { action.invoke(it?.toString()) }
        ?.also(textWatchers::add)

    private fun clearWatchers() = textWatchers.iterator().forEach {
        editText?.removeTextChangedListener(it)
        textWatchers.remove(it)
    }


}