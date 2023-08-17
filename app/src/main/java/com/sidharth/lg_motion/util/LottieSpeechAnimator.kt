package com.sidharth.lg_motion.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.airbnb.lottie.LottieAnimationView

class LottieSpeechAnimator(
    private val context: Context,
    private val animationView: LottieAnimationView,
) {

    interface OnSpeechRecognitionListener {
        fun onSpeechRecognitionResult(result: String)
        fun onSpeechRecognitionError(errorMessage: String)
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var listener: OnSpeechRecognitionListener? = null

    fun setOnSpeechRecognitionListener(listener: OnSpeechRecognitionListener) {
        this.listener = listener
    }

    fun start() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(SpeechRecognitionListener())
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            speechRecognizer?.startListening(intent)
        } else {
            listener?.onSpeechRecognitionError("Speech recognition is not available on this device.")
        }
    }

    fun stop() {
        if (animationView.isAnimating) {
            animationView.cancelAnimation()
        }
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private inner class SpeechRecognitionListener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            if (!animationView.isAnimating) {
                animationView.playAnimation()
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val results = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            results?.get(0)?.let { result ->
                listener?.onSpeechRecognitionResult(result.lowercase())
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.get(0)?.let { result ->
                listener?.onSpeechRecognitionResult(result.lowercase())
                stop()
            }
        }

        override fun onError(error: Int) {
            listener?.onSpeechRecognitionError("Speech recognition error occurred.")
            stop()
        }

        override fun onEndOfSpeech() {
            animationView.cancelAnimation()
        }

        override fun onBeginningOfSpeech() {
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(p0: ByteArray?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}