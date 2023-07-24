package com.sidharth.lg_motion.domain.model

import androidx.annotation.DrawableRes

data class FunActivity(
    @DrawableRes val cover: Int,
    val name: Activity,
) {
    enum class Activity {
        SNAKE, SPACE_INVADER, PACMAN, TETRIS, DINO_RUN, FACE_MASK, MARIO
    }
}
