package com.sidharth.lg_motion.util

import android.annotation.SuppressLint
import android.view.View
import com.google.android.material.snackbar.Snackbar

object NotificationUtils {
    private var snackbar: Snackbar? = null

    @SuppressLint("ShowToast")
    fun showSnackbar(
        view: View,
        message: String,
        anchorView: View?,
    ) {
        snackbar?.dismiss()

        snackbar = Snackbar.make(
            view,
            message,
            1000,
        ).apply {
            anchorView?.let {
                setAnchorView(it)
            }
        }

        snackbar?.show()
    }
}