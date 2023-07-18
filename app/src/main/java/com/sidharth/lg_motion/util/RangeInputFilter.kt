package com.sidharth.lg_motion.util

import android.text.InputFilter
import android.text.Spanned

class RangeInputFilter(
    private val min: Int = 0,
    private val max: Int = 9999,
) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val input = buildString {
            append(dest?.subSequence(0, dstart).toString())
            append(source?.subSequence(start, end))
            append(dest?.subSequence(dend, dest.length))
        }.toIntOrNull()

        return when (input) {
            in min..max -> null
            else -> ""
        }
    }
}