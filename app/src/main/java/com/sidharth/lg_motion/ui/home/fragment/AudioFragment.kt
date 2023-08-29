package com.sidharth.lg_motion.ui.home.fragment

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.sidharth.lg_motion.databinding.FragmentAudioBinding
import com.sidharth.lg_motion.util.LiquidGalaxyManager
import com.sidharth.lg_motion.util.LottieSpeechAnimator
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.SpeechLandmarkerResultParser
import kotlinx.coroutines.launch


class AudioFragment : Fragment(), LottieSpeechAnimator.OnSpeechRecognitionListener {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var lottieSpeechAnimator: LottieSpeechAnimator
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    private var continuousListen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
            setStreamVolume(
                AudioManager.STREAM_MUSIC,
                0,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )
        }
        continuousListen = preferences.getBoolean("continuous_listen", false)
        binding = FragmentAudioBinding.inflate(layoutInflater)
        lottieSpeechAnimator = LottieSpeechAnimator(
            context = requireContext(),
            animationView = binding.animationView,
            listenContinuously = continuousListen
        )
        lottieSpeechAnimator.setOnSpeechRecognitionListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (continuousListen) {
            binding.listen.visibility = View.GONE
            lottieSpeechAnimator.start()
        } else {
            binding.listen.visibility = View.VISIBLE
            binding.listen.setOnClickListener {
                lottieSpeechAnimator.stop()
                lottieSpeechAnimator.start()
            }
        }
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
        if (isAdded) {
            activity?.runOnUiThread {
                binding.result.text = result
            }
        }
        val res = SpeechLandmarkerResultParser.getStateFromSpeechResult(result)
        lifecycleScope.launch {
            res?.let {
                execute(res.first, res.second)
            }
        }
    }

    override fun onSpeechRecognitionError(errorMessage: String) {
        Log.e("LottieSpeechAnimator", errorMessage)
    }

    private fun execute(state: LiquidGalaxyManager.State, direction: String?) {
        if (NetworkUtils.isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                LiquidGalaxyManager.getInstance()?.performAction(
                    state = state,
                    direction = direction
                )
            }
        }
    }
}