package com.sidharth.lg_motion.domain.model

import androidx.annotation.DrawableRes

data class Feature(
    @DrawableRes val cover: Int,
    val title: String,
    val description: String,
    val type: Type
) {
    enum class Type {
        FACE, HAND, AUDIO, POSE, OBJECT
    }
}
