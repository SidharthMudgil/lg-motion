package com.sidharth.lg_motion.util

import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.domain.model.Feature

object Constants {
    val featureList = listOf(
        Feature(
            title = "Facial Movement",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy using facial movements. Utilizes MediaPipe's face landmark detection for precise tracking and control.",
            type = Feature.Type.FACE
        ),
        Feature(
            title = "Hand Gestures",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy using intuitive hand gestures. Leveraging MediaPipe's hand landmark detection for accurate and responsive interaction.",
            type = Feature.Type.HAND
        ),
        Feature(
            title = "Voice-Activated",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy using voice commands and audio input. Employs speech-to-text technology for seamless control and command execution.",
            type = Feature.Type.AUDIO
        ),
        Feature(
            title = "Pose Recognition",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy through poses and body movements. Utilizes MediaPipe's pose landmark detection to interpret and translate physical actions.",
            type = Feature.Type.POSE
        ),
        Feature(
            title = "Object Manipulation",
            cover = R.drawable.ic_launcher_foreground,
            description = "Interact with Liquid Galaxy by detecting and manipulating physical objects. Harnesses the power of MediaPipe's object detection for object recognition and control.",
            type = Feature.Type.OBJECT
        )
    )
}