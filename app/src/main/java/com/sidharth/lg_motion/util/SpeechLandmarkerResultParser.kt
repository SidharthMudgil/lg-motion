package com.sidharth.lg_motion.util

object SpeechLandmarkerResultParser {
    data class FaceLandmarkerProperties(
        val moveNorthGesture: String,
        val moveSouthGesture: String,
        val moveEastGesture: String,
        val moveWestGesture: String,
        val rotateLeftGesture: String,
        val rotateRightGesture: String,
        val zoomInGesture: String,
        val zoomOutGesture: String,
        val moveNorthSensitivity: Float,
        val moveSouthSensitivity: Float,
        val moveEastSensitivity: Float,
        val moveWestSensitivity: Float,
        val rotateLeftSensitivity: Float,
        val rotateRightSensitivity: Float,
        val zoomInSensitivity: Float,
        val zoomOutSensitivity: Float,
    )

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

    private val faceGestureMap = mapOf(
        "Brow Down Left" to "browDownLeft",
        "Brow Down Right" to "browDownRight",
        "Brow Inner Up" to "browInnerUp",
        "Brow Outer Up Left" to "browOuterUpLeft",
        "Brow Outer Up Right" to "browOuterUpRight",
        "Cheek Puff" to "cheekPuff",
        "Cheek Squint Left" to "cheekSquintLeft",
        "Cheek Squint Right" to "cheekSquintRight",
        "Eye Blink Left" to "eyeBlinkLeft",
        "Eye Blink Right" to "eyeBlinkRight",
        "Eye Look Down Left" to "eyeLookDownLeft",
        "Eye Look Down Right" to "eyeLookDownRight",
        "Eye Look In Left" to "eyeLookInLeft",
        "Eye Look In Right" to "eyeLookInRight",
        "Eye Look Out Left" to "eyeLookOutLeft",
        "Eye Look Out Right" to "eyeLookOutRight",
        "Eye Look Up Left" to "eyeLookUpLeft",
        "Eye Look Up Right" to "eyeLookUpRight",
        "Eye Squint Left" to "eyeSquintLeft",
        "Eye Squint Right" to "eyeSquintRight",
        "Eye Wide Left" to "eyeWideLeft",
        "Eye Wide Right" to "eyeWideRight",
        "Jaw Forward" to "jawForward",
        "Jaw Left" to "jawLeft",
        "Jaw Open" to "jawOpen",
        "Jaw Right" to "jawRight",
        "Mouth Close" to "mouthClose",
        "Mouth Dimple Left" to "mouthDimpleLeft",
        "Mouth Dimple Right" to "mouthDimpleRight",
        "Mouth Frown Left" to "mouthFrownLeft",
        "Mouth Frown Right" to "mouthFrownRight",
        "Mouth Funnel" to "mouthFunnel",
        "Mouth Left" to "mouthLeft",
        "Mouth Lower Down Left" to "mouthLowerDownLeft",
        "Mouth Lower Down Right" to "mouthLowerDownRight",
        "Mouth Press Left" to "mouthPressLeft",
        "Mouth Press Right" to "mouthPressRight",
        "Mouth Pucker" to "mouthPucker",
        "Mouth Right" to "mouthRight",
        "Mouth Roll Lower" to "mouthRollLower",
        "Mouth Roll Upper" to "mouthRollUpper",
        "Mouth Shrug Lower" to "mouthShrugLower",
        "Mouth Shrug Upper" to "mouthShrugUpper",
        "Mouth Smile Left" to "mouthSmileLeft",
        "Mouth Smile Right" to "mouthSmileRight",
        "Mouth Stretch Left" to "mouthStretchLeft",
        "Mouth Stretch Right" to "mouthStretchRight",
        "Mouth Upper Up Left" to "mouthUpperUpLeft",
        "Mouth Upper Up Right" to "mouthUpperUpRight",
        "Nose Sneer Left" to "noseSneerLeft",
        "Nose Sneer Right" to "noseSneerRight"
    )

    fun getStateFromFaceLandmarkerResult(
        resultBundle: FaceLandmarkerHelper.ResultBundle,
        properties: FaceLandmarkerProperties,
    ): LiquidGalaxyManager.State {
        val blendshapes = resultBundle.result.faceBlendshapes().get()[0]
            .associateBy { it.categoryName().trim('_') }
            .mapValues { it.value.score() }

        val idleConfidence = blendshapes.getValue("neutral")
        val moveNorthConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.moveNorthGesture))
        val moveSouthConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.moveSouthGesture))
        val moveEastConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.moveEastGesture))
        val moveWestConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.moveWestGesture))
        val rotateLeftConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.rotateLeftGesture))
        val rotateRightConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.rotateRightGesture))
        val zoomInConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.zoomInGesture))
        val zoomOutConfidence =
            blendshapes.getValue(faceGestureMap.getValue(properties.zoomOutGesture))

        return when {
            moveNorthConfidence >= properties.moveNorthSensitivity -> LiquidGalaxyManager.State.MOVE_NORTH
            moveSouthConfidence >= properties.moveSouthSensitivity -> LiquidGalaxyManager.State.MOVE_SOUTH
            moveEastConfidence >= properties.moveEastSensitivity -> LiquidGalaxyManager.State.MOVE_EAST
            moveWestConfidence >= properties.moveWestSensitivity -> LiquidGalaxyManager.State.MOVE_WEST
            rotateLeftConfidence >= properties.rotateLeftSensitivity -> LiquidGalaxyManager.State.ROTATE_LEFT
            rotateRightConfidence >= properties.rotateRightSensitivity -> LiquidGalaxyManager.State.ROTATE_RIGHT
            zoomInConfidence >= properties.zoomInSensitivity -> LiquidGalaxyManager.State.ZOOM_IN
            zoomOutConfidence >= properties.zoomOutSensitivity -> LiquidGalaxyManager.State.ZOOM_OUT
            idleConfidence >= 0.5F -> LiquidGalaxyManager.State.IDLE
            else -> LiquidGalaxyManager.State.IDLE
        }
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

//    fun getStateFromHandLandmarkerResult(
//        resultBundle: HandLandmarkerHelper.ResultBundle
//    ): LiquidGalaxyManager.State {
//        Log.d("resultLandmarksHand", resultBundle.results.toString())
//        return LiquidGalaxyManager.State.IDLE
//    }
//
//    fun getStateFromPoseLandmarkerResult(
//        resultBundle: PoseLandmarkerHelper.ResultBundle
//    ): LiquidGalaxyManager.State {
//        Log.d("resultLandmarksPose", resultBundle.results.toString())
//        return LiquidGalaxyManager.State.IDLE
//    }
//
//    fun getStateFromObjectDetectorResult(
//        resultBundle: ObjectDetectorHelper.ResultBundle
//    ): String {
//        Log.d("resultDetectedObject", resultBundle.results[0].detections()[0].categories()[0].categoryName())
//        return LiquidGalaxyManager.State.IDLE
//    }
}