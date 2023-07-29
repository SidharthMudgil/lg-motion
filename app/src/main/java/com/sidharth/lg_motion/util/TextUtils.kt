package com.sidharth.lg_motion.util

object TextUtils {
    fun isValidIp(text: String): Boolean {
        val ipPattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$".toRegex()
        return text.isNotBlank() && text.matches(ipPattern)
    }
}