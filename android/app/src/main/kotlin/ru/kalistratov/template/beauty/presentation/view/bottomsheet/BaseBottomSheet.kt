package ru.kalistratov.template.beauty.presentation.view.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.kalistratov.template.beauty.R

fun interface BottomSheetOnClosesListener {
    fun onCloses()
}

abstract class BaseBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val PERCENTAGE_OF_TOTAL_HEIGHT = 0.7
    }

    var onClosesListener: BottomSheetOnClosesListener? = null

    override fun onCancel(dialog: DialogInterface) {
        onClosesListener?.onCloses()
        super.onCancel(dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super
        .onCreateDialog(savedInstanceState).also { dialog ->
            if (isFullscreen()) dialog.setOnShowListener {
                setupFullHeight(it as BottomSheetDialog)
            }
        }

    private fun setupFullHeight(sheet: BottomSheetDialog) {
        /*val sheetView = sheet.findViewById<View>(R.id.design_bottom_sheet) ?: return
        val layoutParams = sheetView.layoutParams
        sheetView.layoutParams = layoutParams
            ?.also { it.height = getWindowHeight() }*/
    }

    private fun getWindowHeight() = DisplayMetrics()
        .also(requireActivity().windowManager.defaultDisplay::getMetrics)
        .let { it.heightPixels * PERCENTAGE_OF_TOTAL_HEIGHT }.toInt()

    open fun findView() = Unit

    override fun getTheme() = R.style.BottomSheetDialogTheme

    abstract fun getSheetTag(): String

    abstract fun isFullscreen(): Boolean

}
