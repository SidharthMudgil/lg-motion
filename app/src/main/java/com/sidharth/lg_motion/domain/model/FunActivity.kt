package com.sidharth.lg_motion.domain.model

import androidx.annotation.DrawableRes

data class FunActivity(
    @DrawableRes val cover: Int,
    val name: String,
)