package com.sidharth.lg_motion.ui.home.fragment

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
import com.sidharth.lg_motion.util.LiquidGalaxyStateUtil
import com.sidharth.lg_motion.util.LottieSpeechAnimator
import com.sidharth.lg_motion.util.NetworkUtils
import kotlinx.coroutines.launch


class AudioFragment : Fragment(), LottieSpeechAnimator.OnSpeechRecognitionListener {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var lottieSpeechAnimator: LottieSpeechAnimator

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
        val res = LiquidGalaxyStateUtil.getStateFromSpeechResult(result)
        lifecycleScope.launch {
            res?.let {
                execute(res.first, res.second)
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