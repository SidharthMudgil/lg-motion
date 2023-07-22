package com.sidharth.lg_motion.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sidharth.lg_motion.R

object DialogUtils {
    private var dialog: Dialog? = null

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.no_connection_dialog, null)
        dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setTitle("Liquid Galaxy Connection")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Connect") { dialog, _ ->
                dialog.dismiss()
                // TODO()
            }.create()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    fun show(context: Context) {
        if (dialog == null) {
            init(context)
        }
        dialog?.show()
    }
}