package ru.kalistratov.template.beauty.presentation.view

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class TemplateBottomSheet : BottomSheetDialogFragment() {

    var onClosesListener: BottomSheetOnClosesListener? = null

    fun interface BottomSheetOnClosesListener {
        fun onCloses()
    }
}
