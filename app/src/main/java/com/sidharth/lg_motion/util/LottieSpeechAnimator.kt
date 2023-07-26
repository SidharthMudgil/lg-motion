package com.sidharth.lg_motion.util

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.animation.AccelerateInterpolator
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

    private val animator = with(ValueAnimator.ofFloat(0.0f)) {
        interpolator = AccelerateInterpolator()
        duration = sampling
        addUpdateListener {
            animationView.progress = it.animatedValue as Float
        }
        this
    }

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
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private inner class SpeechRecognitionListener : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            // Optional: You can perform any setup tasks when the recognizer is ready.
        }

        override fun onBeginningOfSpeech() {
            // Optional: You can perform any tasks when the user starts speaking.
        }

        override fun onRmsChanged(rmsdB: Float) {
            val rms = cleanUpSignal(rmsdB)
            val progress = getProgress(rms)
            with(animator) {
                cancel()
                val currentStop = animatedValue as Float
                val maxStop = progress.coerceAtMost(1.0).toFloat()
                setFloatValues(currentStop, maxStop)
                start()
            }
        }

        override fun onBufferReceived(p0: ByteArray?) {
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
                start()
            }
        }

        override fun onError(error: Int) {
            listener?.onSpeechRecognitionError("Speech recognition error occurred.")
            stop()
            start()
        }

        override fun onEndOfSpeech() {
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
        }
    }

    companion object {
        private var ema = 0.0
        private const val alpha = 0.8
        private const val preGain = 0.3
        private const val minDb = 0.0
        private const val maxDb = 80.0
        private const val sampling = 100L

        private fun cleanUpSignal(rms: Float): Double {
            ema = ema * alpha + (1 - alpha) * rms
            val preGained = preGain * ema
            return 20.0 * kotlin.math.log10(preGained)
        }

        private fun getProgress(db: Double): Double {
            var perc = db.coerceAtLeast(minDb) - minDb
            perc /= (maxDb - minDb)
            return perc
        }
    }
}