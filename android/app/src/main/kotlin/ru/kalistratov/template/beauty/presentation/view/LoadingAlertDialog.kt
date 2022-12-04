package ru.kalistratov.template.beauty.presentation.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import ru.kalistratov.template.beauty.R

class LoadingAlertDialog(
    context: Context
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_alert_loading)
        setCancelable(false)
    }

    fun show(show: Boolean) = if (show) show() else hide()
}
