package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sidharth.lg_motion.databinding.FragmentAudioBinding
import com.sidharth.lg_motion.util.LottieSpeechAnimator

class AudioFragment : Fragment(), LottieSpeechAnimator.OnSpeechRecognitionListener {
    private lateinit var binding: FragmentAudioBinding
    private lateinit var lottieSpeechAnimator: LottieSpeechAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        Log.d("speechresult", result)
    }

    override fun onSpeechRecognitionError(errorMessage: String) {
        Log.d("speecherror", errorMessage)
    }
}