package com.sidharth.lg_motion.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sidharth.lg_motion.R

object DialogUtils {
    private var dialog: Dialog? = null

    private fun init(context: Context, callback: () -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.connection_dialog, null)
        dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setTitle(context.getString(R.string.connection_dialog_title))
            .setMessage(context.getString(R.string.connection_dialog_message))
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Connect") { dialog, _ ->
                callback()
                dialog.dismiss()
            }.create()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    fun show(context: Context, callback: () -> Unit) {
        if (dialog == null) {
            init(context, callback)
        }
        dialog?.show()
    }

    fun dismiss() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }
}