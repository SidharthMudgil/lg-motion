package com.sidharth.lg_motion.util

import android.graphics.Bitmap
import androidx.palette.graphics.Palette

object ImageColorExtractor {

    interface ColorExtractionCallback {
        fun onColorsExtracted(colors: Colors)
    }

    data class Colors(
        val vibrant: Int = 0x000000,
        val vibrantLight: Int = 0x000000,
        val vibrantDark: Int = 0x000000,
        val muted: Int = 0x000000,
        val mutedLight: Int = 0x000000,
        val mutedDark: Int = 0x000000
    )

    fun extractColorsAsync(
        bitmap: Bitmap,
        callback: ColorExtractionCallback
    ) {
        Palette.from(bitmap).generate { palette ->
            val colors = Colors(
                vibrant = palette?.getVibrantColor(0x000000) ?: 0x000000,
                vibrantLight = palette?.getLightVibrantColor(0x000000) ?: 0x000000,
                vibrantDark = palette?.getDarkVibrantColor(0x000000) ?: 0x000000,
                muted = palette?.getMutedColor(0x000000) ?: 0x000000,
                mutedLight = palette?.getLightMutedColor(0x000000) ?: 0x000000,
                mutedDark = palette?.getDarkMutedColor(0x000000) ?: 0x000000
            )
            callback.onColorsExtracted(colors)
        }
    }
}
