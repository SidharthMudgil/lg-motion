package com.sidharth.lg_motion.ui.view.fragment

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sidharth.lg_motion.databinding.FragmentAudioBinding
import com.sidharth.lg_motion.util.LiquidGalaxyController
import com.sidharth.lg_motion.util.LottieSpeechAnimator
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.ToastUtil
import kotlinx.coroutines.launch


class AudioFragment : Fragment(), LottieSpeechAnimator.OnSpeechRecognitionListener {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var lottieSpeechAnimator: LottieSpeechAnimator

    enum class COMMAND(
        val keys: List<String>
    ) {
        MOVE_NORTH(listOf("north", "move north", "move up", "up")),
        MOVE_SOUTH(listOf("south", "move south", "move down", "down")),
        MOVE_EAST(listOf("east", "move east", "move right")),
        MOVE_WEST(listOf("west", "move west", "move left")),
        ROTATE_LEFT(listOf("left", "rotate left", "anticlockwise", "rotate anticlockwise")),
        ROTATE_RIGHT(listOf("right", "rotate right", "clockwise", "rotate clockwise")),
        ZOOM_IN(listOf("zoom in", "zoomin", "plus")),
        ZOOM_OUT(listOf("zoom out", "zoomout", "minus")),
        FLY_TO(listOf("fly to", "flyto", "flight", "goto", "go to")),
        CHANGE_PLANET(listOf("planet", "change planet")),
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
            setStreamVolume(
                AudioManager.STREAM_MUSIC,
                0,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )
        }
        binding = FragmentAudioBinding.inflate(layoutInflater)
        lottieSpeechAnimator = LottieSpeechAnimator(requireContext(), binding.animationView)
        lottieSpeechAnimator.setOnSpeechRecognitionListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lottieSpeechAnimator.start()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        lottieSpeechAnimator.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        lottieSpeechAnimator.stop()
    }

    override fun onSpeechRecognitionResult(result: String) {
        val flyToPattern = Regex("${COMMAND.FLY_TO.keys.joinToString("|")}\\s+to\\s+(.+)")
        val planetPattern =
            Regex("${COMMAND.CHANGE_PLANET.keys.joinToString("|")}\\s+(earth|mars|moon)")

        when (result) {
            in COMMAND.MOVE_NORTH.keys -> {
                ToastUtil.showToast(requireContext(), "MOVING NORTH")
                execute(LiquidGalaxyController.State.MOVE_NORTH, null)
            }

            in COMMAND.MOVE_SOUTH.keys -> {
                ToastUtil.showToast(requireContext(), "MOVING SOUTH")
                execute(LiquidGalaxyController.State.MOVE_SOUTH, null)
            }

            in COMMAND.MOVE_EAST.keys -> {
                ToastUtil.showToast(requireContext(), "MOVING EAST")
                execute(LiquidGalaxyController.State.MOVE_EAST, null)
            }

            in COMMAND.MOVE_WEST.keys -> {
                ToastUtil.showToast(requireContext(), "MOVING WEST")
                execute(LiquidGalaxyController.State.MOVE_WEST, null)
            }

            in COMMAND.ROTATE_LEFT.keys -> {
                ToastUtil.showToast(requireContext(), "ROTATING LEFT")
                execute(LiquidGalaxyController.State.ROTATE_LEFT, null)
            }

            in COMMAND.ROTATE_RIGHT.keys -> {
                ToastUtil.showToast(requireContext(), "ROTATING RIGHT")
                execute(LiquidGalaxyController.State.ROTATE_RIGHT, null)
            }

            in COMMAND.ZOOM_IN.keys -> {
                ToastUtil.showToast(requireContext(), "ZOOMING IN")
                execute(LiquidGalaxyController.State.ZOOM_IN, null)
            }

            in COMMAND.ZOOM_OUT.keys -> {
                ToastUtil.showToast(requireContext(), "ZOOMING OUT")
                execute(LiquidGalaxyController.State.ZOOM_OUT, null)
            }
        }

        if (flyToPattern.matches(result)) {
            val destination = flyToPattern.find(result)?.groupValues?.get(1)
            if (!destination.isNullOrBlank()) {
                ToastUtil.showToast(requireContext(), "FLYING TO ${destination.uppercase()}")
                execute(LiquidGalaxyController.State.FLY_TO, destination)
            }
        }

        if (planetPattern.matches(result)) {
            when (val planet = planetPattern.find(result)?.groupValues?.get(1)) {
                "earth", "moon", "mars" -> {
                    ToastUtil.showToast(requireContext(), "PLANET ${planet.uppercase()}")
                    execute(LiquidGalaxyController.State.PLANET, planet)
                }
            }
        }
    }

    override fun onSpeechRecognitionError(errorMessage: String) {
        Log.e("LottieSpeechAnimator", errorMessage)
    }

    private fun execute(state: LiquidGalaxyController.State, direction: String?) {
        if (NetworkUtils.isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                LiquidGalaxyController.getInstance()?.performAction(
                    state = state,
                    direction = direction
                )
            }
        }
    }
}