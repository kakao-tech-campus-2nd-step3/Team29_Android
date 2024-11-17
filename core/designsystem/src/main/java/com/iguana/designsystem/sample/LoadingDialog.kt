package com.iguana.designsystem.sample

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.iguana.designsystem.R

class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, null))
    }

    fun show() {
        if (!dialog.isShowing) dialog.show()
    }

    fun dismiss() {
        if (dialog.isShowing) dialog.dismiss()
    }
}
