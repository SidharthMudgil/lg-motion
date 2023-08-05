package com.sidharth.lg_motion.util

import android.util.Log

object SpeechLandmarkerResultParser {
    private const val faceConfidence = 0.5

    private enum class AudioCommand(
        val keys: List<String>
    ) {
        IDLE(listOf("stop", "stop moving", "idle")),
        MOVE_NORTH(listOf("north", "move north", "move up", "up")),
        MOVE_SOUTH(listOf("south", "move south", "move down", "down")),
        MOVE_EAST(listOf("east", "move east", "move right")),
        MOVE_WEST(listOf("west", "move west", "move left")),
        ROTATE_LEFT(listOf("left", "rotate left", "anticlockwise", "rotate anticlockwise")),
        ROTATE_RIGHT(listOf("right", "rotate right", "clockwise", "rotate clockwise")),
        ZOOM_IN(listOf("zoom in", "zoomin", "plus")),
        ZOOM_OUT(listOf("zoom out", "zoomout", "minus")),
        FLY_TO(listOf("fly to", "flyto", "flight to", "goto", "go to")),
        CHANGE_PLANET(listOf("planet", "change planet")),
    }

    fun getStateFromFaceLandmarkerResult(
        resultBundle: FaceLandmarkerHelper.ResultBundle
    ): LiquidGalaxyManager.State {
        val categoryNames = listOf(
            "neutral", "jawOpen", "jawLeft", "jawRight", "mouthRollUpper", "mouthRollLower",
            "eyeBlinkRight", "eyeBlinkLeft", "eyeSquintLeft", "eyeSquintRight"
        )
        val blendshapes = resultBundle.result.faceBlendshapes().get()[0]
            .associateBy { it.categoryName().trim('_') }
            .filterKeys { it in categoryNames }
            .mapValues { it.value.score() }

        val neutral = blendshapes.getValue("neutral")
        val eyeBlinkLeft = blendshapes.getValue("eyeBlinkLeft")
        val eyeBlinkRight = blendshapes.getValue("eyeBlinkRight")
        val eyeSquintLeft = blendshapes.getValue("eyeSquintLeft")
        val eyeSquintRight = blendshapes.getValue("eyeSquintRight")
        val jawLeft = blendshapes.getValue("jawLeft")
        val jawOpen = blendshapes.getValue("jawOpen")
        val jawRight = blendshapes.getValue("jawRight")
        val mouthRollLower = blendshapes.getValue("mouthRollLower")
        val mouthRollUpper = blendshapes.getValue("mouthRollUpper")

        return when {
            jawRight > faceConfidence -> LiquidGalaxyManager.State.MOVE_EAST
            jawLeft > faceConfidence -> LiquidGalaxyManager.State.MOVE_WEST
            mouthRollUpper > faceConfidence -> LiquidGalaxyManager.State.MOVE_NORTH
            mouthRollLower > faceConfidence -> LiquidGalaxyManager.State.MOVE_SOUTH
            eyeBlinkRight > faceConfidence && eyeBlinkLeft > faceConfidence || eyeSquintRight > faceConfidence && eyeSquintLeft > faceConfidence -> LiquidGalaxyManager.State.ZOOM_OUT
            eyeBlinkLeft > faceConfidence || eyeSquintLeft > faceConfidence -> LiquidGalaxyManager.State.ROTATE_LEFT
            eyeBlinkRight > faceConfidence || eyeSquintRight > faceConfidence -> LiquidGalaxyManager.State.ROTATE_RIGHT
            jawOpen > faceConfidence -> LiquidGalaxyManager.State.ZOOM_IN
            neutral > faceConfidence -> LiquidGalaxyManager.State.IDLE
            else -> LiquidGalaxyManager.State.IDLE
        }
    }

    fun getStateFromHandLandmarkerResult(
        resultBundle: HandLandmarkerHelper.ResultBundle
    ): LiquidGalaxyManager.State {
        Log.d("resultLandmarksHand", resultBundle.results.toString())
        return LiquidGalaxyManager.State.IDLE
    }

    fun getStateFromPoseLandmarkerResult(
        resultBundle: PoseLandmarkerHelper.ResultBundle
    ): LiquidGalaxyManager.State {
        Log.d("resultLandmarksPose", resultBundle.results.toString())
        return LiquidGalaxyManager.State.IDLE
    }

    fun getStateFromObjectDetectorResult(
        resultBundle: ObjectDetectorHelper.ResultBundle
    ): LiquidGalaxyManager.State {
        Log.d("resultObject", resultBundle.results.toString())
        return LiquidGalaxyManager.State.IDLE
    }

    fun getStateFromSpeechResult(
        result: String
    ): Pair<LiquidGalaxyManager.State, String?>? {
        val flyToPattern = Regex("\\b(${AudioCommand.FLY_TO.keys.joinToString("|")})\\s+(\\w+)")
        val planetPattern =
            Regex("\\b(${AudioCommand.CHANGE_PLANET.keys.joinToString("|")})\\s+(earth|mars|moon)")

        return when {
            result in AudioCommand.IDLE.keys -> Pair(LiquidGalaxyManager.State.IDLE, null)
            result in AudioCommand.MOVE_NORTH.keys -> Pair(
                LiquidGalaxyManager.State.MOVE_NORTH,
                null
            )

            result in AudioCommand.MOVE_SOUTH.keys -> Pair(
                LiquidGalaxyManager.State.MOVE_SOUTH,
                null
            )

            result in AudioCommand.MOVE_EAST.keys -> Pair(
                LiquidGalaxyManager.State.MOVE_EAST,
                null
            )

            result in AudioCommand.MOVE_WEST.keys -> Pair(
                LiquidGalaxyManager.State.MOVE_WEST,
                null
            )

            result in AudioCommand.ZOOM_IN.keys -> Pair(LiquidGalaxyManager.State.ZOOM_IN, null)
            result in AudioCommand.ZOOM_OUT.keys -> Pair(
                LiquidGalaxyManager.State.ZOOM_OUT,
                null
            )

            result in AudioCommand.ROTATE_LEFT.keys -> Pair(
                LiquidGalaxyManager.State.ROTATE_LEFT, null
            )

            result in AudioCommand.ROTATE_RIGHT.keys -> Pair(
                LiquidGalaxyManager.State.ROTATE_RIGHT, null
            )

            flyToPattern.matches(result) -> {
                val destination = flyToPattern.find(result)?.groupValues?.get(2)
                if (!destination.isNullOrBlank()) Pair(
                    LiquidGalaxyManager.State.FLY_TO,
                    destination
                )
                else null
            }

            planetPattern.matches(result) -> {
                val planet = planetPattern.find(result)?.groupValues?.get(2)
                if (!planet.isNullOrBlank()) Pair(LiquidGalaxyManager.State.PLANET, planet)
                else null
            }

            else -> null
        }
    }
}