package ru.kalistratov.template.beauty.presentation.view.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.kalistratov.template.beauty.R

class DayDetailsBottomSheet : BaseBottomSheet() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_day_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog).behavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun getSheetTag() = "DayDetailsBottomSheet"

    override fun isFullscreen() = true

    override fun onCancel(dialog: DialogInterface) {
        onClosesListener?.onCloses()
        super.onCancel(dialog)
    }
}
