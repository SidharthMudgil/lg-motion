package com.sidharth.lg_motion.util

import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.domain.model.Feature
import com.sidharth.lg_motion.domain.model.FunActivity

object Constants {
    val featureList = listOf(
        Feature(
            title = "Facial Movement",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy using facial movements",
            type = Feature.Type.FACE
        ),
        Feature(
            title = "Hand Gestures",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy using intuitive hand gestures",
            type = Feature.Type.HAND
        ),
        Feature(
            title = "Voice Activated",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy using voice commands and audio input",
            type = Feature.Type.VOICE
        ),
        Feature(
            title = "Pose Recognition",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy through poses and body movements",
            type = Feature.Type.POSE
        ),
        Feature(
            title = "Object Manipulation",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy by manipulating physical objects",
            type = Feature.Type.OBJECT
        )
    )

    val funActivities = listOf(
        FunActivity(
            cover = R.drawable.ic_launcher_foreground,
            name = FunActivity.Activity.FACE_MASK
        ),
        FunActivity(
            cover = R.drawable.ic_launcher_foreground,
            name = FunActivity.Activity.SNAKE
        ),
        FunActivity(
            cover = R.drawable.ic_launcher_foreground,
            name = FunActivity.Activity.ASTEROIDS
        ),
        FunActivity(
            cover = R.drawable.ic_launcher_foreground,
            name = FunActivity.Activity.DINO_RUN
        ),
        FunActivity(
            cover = R.drawable.ic_launcher_foreground,
            name = FunActivity.Activity.MARIO
        ),
        FunActivity(
            cover = R.drawable.ic_launcher_foreground,
            name = FunActivity.Activity.PACMAN
        ),
        FunActivity(
            cover = R.drawable.ic_launcher_foreground,
            name = FunActivity.Activity.TETRIS
        ),
    )
}