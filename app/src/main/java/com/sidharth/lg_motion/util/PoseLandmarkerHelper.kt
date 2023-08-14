package com.sidharth.lg_motion.util

//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.SystemClock
//import android.util.Log
//import androidx.annotation.VisibleForTesting
//import androidx.camera.core.ImageProxy
//import com.google.mediapipe.framework.image.BitmapImageBuilder
//import com.google.mediapipe.framework.image.MPImage
//import com.google.mediapipe.tasks.core.BaseOptions
//import com.google.mediapipe.tasks.core.Delegate
//import com.google.mediapipe.tasks.vision.core.RunningMode
//import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
//import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
//
//class PoseLandmarkerHelper(
//    val context: Context,
//    val poseLandmarkerHelperListener: LandmarkerListener? = null
//) {
//
//    private var poseLandmarker: PoseLandmarker? = null
//
//    init {
//        setupPoseLandmarker()
//    }
//
//    fun clearPoseLandmarker() {
//        poseLandmarker?.close()
//        poseLandmarker = null
//    }
//
//    fun isClose(): Boolean {
//        return poseLandmarker == null
//    }
//
//    fun setupPoseLandmarker() {
//        try {
//            val baseOptions = BaseOptions.builder()
//                .setDelegate(DELEGATE)
//                .setModelAssetPath(MODEL_POSE_LANDMARKER_FULL)
//                .build()
//
//            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
//                .setBaseOptions(baseOptions)
//                .setMinPoseDetectionConfidence(MIN_POSE_DETECTION_CONFIDENCE)
//                .setMinTrackingConfidence(MIN_POSE_TRACKING_CONFIDENCE)
//                .setMinPosePresenceConfidence(MIN_POSE_PRESENCE_CONFIDENCE)
//                .setRunningMode(RUNNING_MODE)
//                .setResultListener(this::returnLivestreamResult)
//                .setErrorListener(this::returnLivestreamError)
//                .build()
//
//            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
//        } catch (e: IllegalStateException) {
//            poseLandmarkerHelperListener?.onError("Pose Landmarker failed to initialize")
//            Log.e(TAG, "MediaPipe failed to load the task with error: " + e.message)
//        } catch (e: RuntimeException) {
//            poseLandmarkerHelperListener?.onError("Pose Landmarker failed to initialize", GPU_ERROR)
//            Log.e(TAG, "Image classifier failed to load model with error: " + e.message)
//        }
//    }
//
//    fun detectLiveStream(
//        imageProxy: ImageProxy,
//        isFrontCamera: Boolean
//    ) {
//        val frameTime = SystemClock.uptimeMillis()
//
//        val bitmapBuffer =
//            Bitmap.createBitmap(
//                imageProxy.width,
//                imageProxy.height,
//                Bitmap.Config.ARGB_8888
//            )
//
//        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//        imageProxy.close()
//
//        val matrix = Matrix().apply {
//            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//
//            if (isFrontCamera) {
//                postScale(
//                    -1f,
//                    1f,
//                    imageProxy.width.toFloat(),
//                    imageProxy.height.toFloat()
//                )
//            }
//        }
//        val rotatedBitmap = Bitmap.createBitmap(
//            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//            matrix, true
//        )
//
//        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
//
//        detectAsync(mpImage, frameTime)
//    }
//
//    @VisibleForTesting
//    fun detectAsync(mpImage: MPImage, frameTime: Long) {
//        poseLandmarker?.detectAsync(mpImage, frameTime)
//    }
//
//    private fun returnLivestreamResult(
//        result: PoseLandmarkerResult,
//        input: MPImage
//    ) {
//        if (result.landmarks().size > 0) {
//            val finishTimeMs = SystemClock.uptimeMillis()
//            val inferenceTime = finishTimeMs - result.timestampMs()
//
//            poseLandmarkerHelperListener?.onResults(
//                ResultBundle(
//                    listOf(result),
//                    inferenceTime,
//                    input.height,
//                    input.width
//                )
//            )
//        } else {
//            poseLandmarkerHelperListener?.onNoResults()
//        }
//    }
//
//    private fun returnLivestreamError(error: RuntimeException) {
//        poseLandmarkerHelperListener?.onError(
//            error.message ?: "An unknown error has occurred"
//        )
//    }
//
//    companion object {
//        const val TAG = "PoseLandmarkerHelper"
//        private const val MODEL_POSE_LANDMARKER_FULL = "pose_landmarker_full.task"
//        const val MIN_POSE_DETECTION_CONFIDENCE = 0.5F
//        const val MIN_POSE_TRACKING_CONFIDENCE = 0.5F
//        const val MIN_POSE_PRESENCE_CONFIDENCE = 0.5F
//        const val OTHER_ERROR = 0
//        const val GPU_ERROR = 1
//        val DELEGATE = Delegate.CPU
//        val RUNNING_MODE = RunningMode.LIVE_STREAM
//    }
//
//    data class ResultBundle(
//        val results: List<PoseLandmarkerResult>,
//        val inferenceTime: Long,
//        val inputImageHeight: Int,
//        val inputImageWidth: Int,
//    )
//
//    interface LandmarkerListener {
//        fun onError(error: String, errorCode: Int = OTHER_ERROR)
//        fun onResults(resultBundle: ResultBundle)
//        fun onNoResults()
//    }
//}