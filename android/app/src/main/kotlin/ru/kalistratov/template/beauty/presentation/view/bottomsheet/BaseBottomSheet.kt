package ru.kalistratov.template.beauty.presentation.view.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.kalistratov.template.beauty.R

fun interface BottomSheetOnClosesListener {
    fun onCloses()
}

open class BaseBottomSheet : BottomSheetDialogFragment() {

    var onClosesListener: BottomSheetOnClosesListener? = null

    override fun onCancel(dialog: DialogInterface) {
        onClosesListener?.onCloses()
        super.onCancel(dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView()
    }

    open fun findView() = Unit

    override fun getTheme() = R.style.BottomSheetDialogTheme
}
