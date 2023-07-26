package com.sidharth.lg_motion.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class FaceLandmarkerHelper(
    val context: Context,
    val faceLandmarkerHelperListener: LandmarkerListener? = null
) {
    private var faceLandmarker: FaceLandmarker? = null

    init {
        setupFaceLandmarker()
    }

    fun clearFaceLandmarker() {
        faceLandmarker?.close()
        faceLandmarker = null
    }

    fun isClose(): Boolean {
        return faceLandmarker == null
    }

    fun setupFaceLandmarker() {
        val baseOptionBuilder = BaseOptions.builder()
            .setDelegate(DELEGATE_CPU)
            .setModelAssetPath(MP_FACE_LANDMARKER_TASK)

        try {
            val baseOptions = baseOptionBuilder.build()

            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinFaceDetectionConfidence(MIN_FACE_DETECTION_CONFIDENCE)
                .setMinTrackingConfidence(MIN_FACE_TRACKING_CONFIDENCE)
                .setMinFacePresenceConfidence(MIN_FACE_PRESENCE_CONFIDENCE)
                .setNumFaces(MAX_NUM_FACES)
                .setRunningMode(MODE_LIVE_STREAM)
                .setResultListener(this::returnLivestreamResult)
                .setErrorListener(this::returnLivestreamError)
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            faceLandmarkerHelperListener?.onError("Face Landmarker failed to initialize")
            Log.e(TAG, "MediaPipe failed to load the task with error: " + e.message)
        } catch (e: RuntimeException) {
            faceLandmarkerHelperListener?.onError("Face Landmarker failed to initialize", GPU_ERROR)
            Log.e(TAG, "Face Landmarker failed to load model with error: " + e.message)
        }
    }

    fun detectLiveStream(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean
    ) {
        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )

        imageProxy.use {
            bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
        }
        imageProxy.close()

        val matrix = Matrix().apply {
            // Rotate the frame received from the camera to be in the same direction as it'll be shown
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            // flip image if user use front camera
            if (isFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    imageProxy.width.toFloat(),
                    imageProxy.height.toFloat()
                )
            }
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        // Convert the input Bitmap object to an MPImage object to run inference
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        detectAsync(mpImage, frameTime)
    }

    // Run face face landmark using MediaPipe Face Landmarker API
    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long) {
        faceLandmarker?.detectAsync(mpImage, frameTime)
        // As we're using running mode LIVE_STREAM, the landmark result will
        // be returned in returnLivestreamResult function
    }

    private fun returnLivestreamResult(
        result: FaceLandmarkerResult,
        input: MPImage
    ) {
        if (result.faceLandmarks().size > 0) {
            val finishTimeMs = SystemClock.uptimeMillis()
            val inferenceTime = finishTimeMs - result.timestampMs()

            faceLandmarkerHelperListener?.onResults(
                ResultBundle(
                    result,
                    inferenceTime,
                    input.height,
                    input.width
                )
            )
        } else {
            faceLandmarkerHelperListener?.onEmpty()
        }
    }

    private fun returnLivestreamError(error: RuntimeException) {
        faceLandmarkerHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    companion object {
        const val TAG = "FaceLandmarkerHelper"
        private const val MP_FACE_LANDMARKER_TASK = "face_landmarker.task"
        const val MIN_FACE_DETECTION_CONFIDENCE = 0.5F
        const val MIN_FACE_TRACKING_CONFIDENCE = 0.5F
        const val MIN_FACE_PRESENCE_CONFIDENCE = 0.5F
        const val MAX_NUM_FACES = 1
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
        val DELEGATE_CPU = Delegate.CPU
        val DELEGATE_GPU = Delegate.GPU
        val MODE_IMAGE = RunningMode.IMAGE
        val MODE_VIDEO = RunningMode.VIDEO
        val MODE_LIVE_STREAM = RunningMode.LIVE_STREAM
    }

    data class ResultBundle(
        val result: FaceLandmarkerResult,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    data class VideoResultBundle(
        val results: List<FaceLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
        fun onEmpty() {}
    }
}