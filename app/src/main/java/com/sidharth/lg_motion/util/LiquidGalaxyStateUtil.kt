package com.sidharth.lg_motion.util

object LiquidGalaxyStateUtil {
    private const val confidence = 0.5

    private enum class Command(
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
    ): LiquidGalaxyController.State {
        val categoryNames = listOf(
            "neutral", "jawOpen", "jawLeft", "jawRight", "mouthRollUpper", "mouthRollLower",
            "eyeBlinkRight", "eyeBlinkLeft", "eyeSquintLeft", "eyeSquintRight"
        )
        val blendshapeMap = resultBundle.result.faceBlendshapes().get()[0]
            .associateBy { it.categoryName().trim('_') }
            .filterKeys { it in categoryNames }
            .mapValues { it.value.score() }

        val neutral = blendshapeMap.getValue("neutral")
        val eyeBlinkLeft = blendshapeMap.getValue("eyeBlinkLeft")
        val eyeBlinkRight = blendshapeMap.getValue("eyeBlinkRight")
        val eyeSquintLeft = blendshapeMap.getValue("eyeSquintLeft")
        val eyeSquintRight = blendshapeMap.getValue("eyeSquintRight")
        val jawLeft = blendshapeMap.getValue("jawLeft")
        val jawOpen = blendshapeMap.getValue("jawOpen")
        val jawRight = blendshapeMap.getValue("jawRight")
        val mouthRollLower = blendshapeMap.getValue("mouthRollLower")
        val mouthRollUpper = blendshapeMap.getValue("mouthRollUpper")

        return when {
            jawRight > confidence -> LiquidGalaxyController.State.MOVE_EAST
            jawLeft > confidence -> LiquidGalaxyController.State.MOVE_WEST
            mouthRollUpper > confidence -> LiquidGalaxyController.State.MOVE_NORTH
            mouthRollLower > confidence -> LiquidGalaxyController.State.MOVE_SOUTH
            eyeBlinkRight > confidence && eyeBlinkLeft > confidence || eyeSquintRight > confidence && eyeSquintLeft > confidence -> LiquidGalaxyController.State.ZOOM_OUT
            eyeBlinkLeft > confidence || eyeSquintLeft > confidence -> LiquidGalaxyController.State.ROTATE_LEFT
            eyeBlinkRight > confidence || eyeSquintRight > confidence -> LiquidGalaxyController.State.ROTATE_RIGHT
            jawOpen > confidence -> LiquidGalaxyController.State.ZOOM_IN
            neutral > confidence -> LiquidGalaxyController.State.IDLE
            else -> LiquidGalaxyController.State.IDLE
        }
    }

    fun getStateFromHandLandmarkerResult(
        resultBundle: HandLandmarkerHelper.ResultBundle
    ): LiquidGalaxyController.State {
//        TODO("")
        return LiquidGalaxyController.State.IDLE
    }

    fun getStateFromPoseLandmarkerResult(
        resultBundle: PoseLandmarkerHelper.ResultBundle
    ): LiquidGalaxyController.State {
//        TODO("")
        return LiquidGalaxyController.State.IDLE
    }

    fun getStateFromObjectDetectorResult(
        resultBundle: ObjectDetectorHelper.ResultBundle
    ): LiquidGalaxyController.State {
//        TODO("")
        return LiquidGalaxyController.State.IDLE
    }

    fun getStateFromSpeechResult(
        result: String
    ): Pair<LiquidGalaxyController.State, String?>? {
        val flyToPattern = Regex("\\b(${Command.FLY_TO.keys.joinToString("|")})\\s+(\\w+)")
        val planetPattern =
            Regex("\\b(${Command.CHANGE_PLANET.keys.joinToString("|")})\\s+(earth|mars|moon)")

        return when {
            result in Command.IDLE.keys -> Pair(LiquidGalaxyController.State.IDLE, null)
            result in Command.MOVE_NORTH.keys -> Pair(LiquidGalaxyController.State.MOVE_NORTH, null)
            result in Command.MOVE_SOUTH.keys -> Pair(LiquidGalaxyController.State.MOVE_SOUTH, null)
            result in Command.MOVE_EAST.keys -> Pair(LiquidGalaxyController.State.MOVE_EAST, null)
            result in Command.MOVE_WEST.keys -> Pair(LiquidGalaxyController.State.MOVE_WEST, null)
            result in Command.ZOOM_IN.keys -> Pair(LiquidGalaxyController.State.ZOOM_IN, null)
            result in Command.ZOOM_OUT.keys -> Pair(LiquidGalaxyController.State.ZOOM_OUT, null)
            result in Command.ROTATE_LEFT.keys -> Pair(
                LiquidGalaxyController.State.ROTATE_LEFT, null
            )

            result in Command.ROTATE_RIGHT.keys -> Pair(
                LiquidGalaxyController.State.ROTATE_RIGHT, null
            )

            flyToPattern.matches(result) -> {
                val destination = flyToPattern.find(result)?.groupValues?.get(2)
                if (!destination.isNullOrBlank()) Pair(
                    LiquidGalaxyController.State.FLY_TO,
                    destination
                )
                else null
            }

            planetPattern.matches(result) -> {
                val planet = planetPattern.find(result)?.groupValues?.get(2)
                if (!planet.isNullOrBlank()) Pair(LiquidGalaxyController.State.PLANET, planet)
                else null
            }

            else -> null
        }
    }
}