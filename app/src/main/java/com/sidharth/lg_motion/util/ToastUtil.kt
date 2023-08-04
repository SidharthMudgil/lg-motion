package com.sidharth.lg_motion.util

import android.content.Context
import android.widget.Toast

object ToastUtil {
    private var sToast: Toast? = null

    fun showToast(context: Context, message: String) {
        sToast?.cancel()
        sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        sToast?.show()
    }
}