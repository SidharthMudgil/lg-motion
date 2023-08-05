package com.sidharth.lg_motion.domain.callback
interface ProgressIndicatorCallback {
    fun showProgressIndicator()
    fun hideProgressIndicator()

    fun showSnackbar(message: String)
}