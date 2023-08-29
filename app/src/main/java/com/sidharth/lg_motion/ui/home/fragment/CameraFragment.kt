package com.sidharth.lg_motion.ui.home.fragment

//import com.sidharth.lg_motion.util.HandLandmarkerHelper
//import com.sidharth.lg_motion.util.ObjectDetectorHelper
//import com.sidharth.lg_motion.util.PoseLandmarkerHelper
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.databinding.FragmentCameraBinding
import com.sidharth.lg_motion.domain.callback.ProgressIndicatorCallback
import com.sidharth.lg_motion.domain.model.Feature
import com.sidharth.lg_motion.util.FaceLandmarkerHelper
import com.sidharth.lg_motion.util.LiquidGalaxyManager
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.SpeechLandmarkerResultParser
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment() {

    private var lastState: LiquidGalaxyManager.State = LiquidGalaxyManager.State.IDLE
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    private val args: CameraFragmentArgs by navArgs()

    private lateinit var faceLandmarkerHelper: FaceLandmarkerHelper
//    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
//    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
//    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    private lateinit var faceLandmarkerProperties: SpeechLandmarkerResultParser.FaceLandmarkerProperties

    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    private lateinit var backgroundExecutor: ExecutorService

    private val fragmentCameraBinding
        get() = _fragmentCameraBinding!!


    override fun onResume() {
        super.onResume()
        backgroundExecutor.execute {
            when (args.feature) {
                Feature.Type.FACE.name -> {
                    if (faceLandmarkerHelper.isClose()) {
                        faceLandmarkerHelper.setupFaceLandmarker()
                    }
                }

//                Feature.Type.HAND.name -> {
//                    if (handLandmarkerHelper.isClose()) {
//                        handLandmarkerHelper.setupHandLandmarker()
//                    }
//                }
//
//                Feature.Type.POSE.name -> {
//                    if (poseLandmarkerHelper.isClose()) {
//                        poseLandmarkerHelper.setupPoseLandmarker()
//                    }
//                }
//
//                Feature.Type.OBJECT.name -> {
//                    if (objectDetectorHelper.isClose()) {
//                        objectDetectorHelper.setupObjectDetector()
//                    }
//                }

                else -> {
                    if (faceLandmarkerHelper.isClose()) {
                        faceLandmarkerHelper.setupFaceLandmarker()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        when (args.feature) {
            Feature.Type.FACE.name -> {
                if (this::faceLandmarkerHelper.isInitialized) {
                    backgroundExecutor.execute {
                        faceLandmarkerHelper.clearFaceLandmarker()
                    }
                }
            }

//            Feature.Type.HAND.name -> {
//                if (this::handLandmarkerHelper.isInitialized) {
//                    backgroundExecutor.execute {
//                        handLandmarkerHelper.clearHandLandmarker()
//                    }
//                }
//            }
//
//            Feature.Type.POSE.name -> {
//                if (this::poseLandmarkerHelper.isInitialized) {
//                    backgroundExecutor.execute {
//                        poseLandmarkerHelper.clearPoseLandmarker()
//                    }
//                }
//            }
//
//            Feature.Type.OBJECT.name -> {
//                if (this::objectDetectorHelper.isInitialized) {
//                    backgroundExecutor.execute {
//                        objectDetectorHelper.clearObjectDetector()
//                    }
//                }
//            }
        }
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraFacing = when (preferences.getBoolean("rear_facing_camera", false)) {
            true -> CameraSelector.LENS_FACING_BACK
            else -> CameraSelector.LENS_FACING_FRONT
        }
        faceLandmarkerProperties = SpeechLandmarkerResultParser.FaceLandmarkerProperties(
            moveNorthGesture = preferences.getString("move_north_gesture", "null") ?: "null",
            moveNorthSensitivity = preferences.getInt("move_north_sensitivity", 5) / 10.toFloat(),

            moveSouthGesture = preferences.getString("move_south_gesture", "null") ?: "null",
            moveSouthSensitivity = preferences.getInt("move_south_sensitivity", 5) / 10.toFloat(),

            moveEastGesture = preferences.getString("move_east_gesture", "null") ?: "null",
            moveEastSensitivity = preferences.getInt("move_east_sensitivity", 5) / 10.toFloat(),

            moveWestGesture = preferences.getString("move_west_gesture", "null") ?: "null",
            moveWestSensitivity = preferences.getInt("move_west_sensitivity", 5) / 10.toFloat(),

            rotateLeftGesture = preferences.getString("rotate_left_gesture", "null") ?: "null",
            rotateLeftSensitivity = preferences.getInt("rotate_left_sensitivity", 5) / 10.toFloat(),

            rotateRightGesture = preferences.getString("rotate_right_gesture", "null") ?: "null",
            rotateRightSensitivity = preferences.getInt(
                "rotate_right_sensitivity",
                5
            ) / 10.toFloat(),

            zoomInGesture = preferences.getString("zoom_in_gesture", "null") ?: "null",
            zoomInSensitivity = preferences.getInt("zoom_in_sensitivity", 5) / 10.toFloat(),

            zoomOutGesture = preferences.getString("zoom_out_gesture", "null") ?: "null",
            zoomOutSensitivity = preferences.getInt("zoom_out_sensitivity", 5) / 10.toFloat()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater)
        when (args.feature) {
            Feature.Type.FACE.name -> setupFacialGestureImages()
        }
        return fragmentCameraBinding.root
    }

    private fun setupFacialGestureImages() {
        _fragmentCameraBinding?.infoLayout?.ivCommand1?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("move_north_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand2?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("move_south_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand3?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("move_east_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand4?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("move_west_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand5?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("rotate_left_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand6?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("rotate_right_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand7?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("zoom_in_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand8?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                faceGestureDrawableMap.getValue(
                    preferences.getString("zoom_out_gesture", "null") ?: "null"
                )
            )
        )

        _fragmentCameraBinding?.infoLayout?.ivCommand9?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.img_neutral,
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backgroundExecutor = Executors.newSingleThreadExecutor()
        if (args.feature != Feature.Type.OBJECT.name) {
            fragmentCameraBinding.viewFinder.post {
                setUpCamera()
            }
        }

        backgroundExecutor.execute {
            when (args.feature) {
                Feature.Type.FACE.name -> {
                    faceLandmarkerHelper = FaceLandmarkerHelper(
                        context = requireContext(),
                        faceLandmarkerHelperListener = faceLandMarkerListener,
                        minFaceDetectionConfidence = preferences.getInt(
                            "face_detection_confidence",
                            5
                        ) / 10.toFloat(),
                        minFaceTrackingConfidence = preferences.getInt(
                            "face_tracking_confidence",
                            5
                        ) / 10.toFloat(),
                        minFacePresenceConfidence = preferences.getInt(
                            "face_presence_confidence",
                            5
                        ) / 10.toFloat(),
                    )
                }

//                Feature.Type.HAND.name -> {
//                    handLandmarkerHelper = HandLandmarkerHelper(
//                        context = requireContext(),
//                        handLandmarkerHelperListener = handLandmarkerListener,
//                    )
//                }
//
//                Feature.Type.POSE.name -> {
//                    poseLandmarkerHelper = PoseLandmarkerHelper(
//                        context = requireContext(),
//                        poseLandmarkerHelperListener = poseLandMarkerListener,
//                    )
//                }
//
//                Feature.Type.OBJECT.name -> {
//                    objectDetectorHelper = ObjectDetectorHelper(
//                        context = requireContext(),
//                        objectDetectorListener = objectDetectorListener,
//                    )
//
//                    fragmentCameraBinding.viewFinder.post {
//                        setUpCamera()
//                    }
//                }
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraFacing).build()

        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                when (args.feature) {
                    Feature.Type.FACE.name -> {
                        it.setAnalyzer(backgroundExecutor) { image ->
                            detectFace(image)
                        }
                    }

//                    Feature.Type.HAND.name -> {
//                        it.setAnalyzer(backgroundExecutor) { image ->
//                            detectHand(image)
//                        }
//                    }
//
//                    Feature.Type.POSE.name -> {
//                        it.setAnalyzer(backgroundExecutor) { image ->
//                            detectPose(image)
//                        }
//                    }
//
//                    Feature.Type.OBJECT.name -> {
//                        it.setAnalyzer(backgroundExecutor) { image ->
//                            detectObject(image)
//                        }
//                    }
                }
            }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(
                fragmentCameraBinding.viewFinder.surfaceProvider
            )
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = fragmentCameraBinding.viewFinder.display.rotation
    }

    private fun detectFace(imageProxy: ImageProxy) {
        faceLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
        )
    }

//    private fun detectHand(imageProxy: ImageProxy) {
//        handLandmarkerHelper.detectLiveStream(
//            imageProxy = imageProxy,
//            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
//        )
//    }
//
//    private fun detectPose(imageProxy: ImageProxy) {
//        poseLandmarkerHelper.detectLiveStream(
//            imageProxy = imageProxy,
//            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
//        )
//    }
//
//    private fun detectObject(imageProxy: ImageProxy) {
//        objectDetectorHelper.detectLivestream(
//            imageProxy = imageProxy,
//            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
//        )
//    }

    private val faceLandMarkerListener = object : FaceLandmarkerHelper.LandmarkerListener {
        override fun onResults(resultBundle: FaceLandmarkerHelper.ResultBundle) {
            val newState = SpeechLandmarkerResultParser.getStateFromFaceLandmarkerResult(
                resultBundle,
                faceLandmarkerProperties
            )
            if ((lastState == newState).not()) {
                lastState = newState
                execute(newState, null)
            }
        }

        override fun onError(error: String, errorCode: Int) {
            showError(error)
        }

        override fun onNoResults() {
            idleState()
        }
    }

//    private val handLandmarkerListener = object : HandLandmarkerHelper.LandmarkerListener {
//        override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {
//            val newState = SpeechLandmarkerResultParser.getStateFromHandLandmarkerResult(resultBundle)
//            if ((lastState == newState).not()) {
//                lastState = newState
//                execute(newState, null)
//            }
//        }
//
//        override fun onError(error: String, errorCode: Int) {
//            showError(error)
//        }
//
//
//        override fun onNoResults() {
//            idleState()
//        }
//    }
//
//    private val poseLandMarkerListener = object : PoseLandmarkerHelper.LandmarkerListener {
//        override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
//            val newState = SpeechLandmarkerResultParser.getStateFromPoseLandmarkerResult(resultBundle)
//            if ((lastState == newState).not()) {
//                lastState = newState
//                execute(newState, null)
//            }
//        }
//
//        override fun onError(error: String, errorCode: Int) {
//            showError(error)
//        }
//
//        override fun onNoResults() {
//            idleState()
//        }
//    }
//
//    private val objectDetectorListener = object : ObjectDetectorHelper.DetectorListener {
//        override fun onResults(resultBundle: ObjectDetectorHelper.ResultBundle) {
//            showSnackbar(SpeechLandmarkerResultParser.getStateFromObjectDetectorResult(resultBundle))
//            val newState = SpeechLandmarkerResultParser.getStateFromObjectDetectorResult(resultBundle)
//            if ((lastState == newState).not()) {
//                lastState = newState
//                execute(newState, null)
//            }
//        }
//
//        override fun onError(error: String, errorCode: Int) {
//            showError(error)
//        }
//
//        override fun onNoResults() {
//            idleState()
//        }
//    }

    private fun showError(error: String) {
        activity?.runOnUiThread {
            showSnackbar(error)
        }
    }

    private fun idleState() {
        lifecycleScope.launch {
            if (lastState != LiquidGalaxyManager.State.IDLE) {
                lastState = LiquidGalaxyManager.State.IDLE
                LiquidGalaxyManager.getInstance()?.performAction(
                    LiquidGalaxyManager.State.IDLE, null
                )
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun execute(state: LiquidGalaxyManager.State, direction: String?) {
        if (isAdded) {
            activity?.runOnUiThread {
                showSnackbar(state.name)
            }
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

    private fun showSnackbar(message: String) {
        if (activity is ProgressIndicatorCallback) {
            (activity as ProgressIndicatorCallback?)?.showSnackbar(message)
        }
    }

    companion object {
        private const val TAG = "Landmarker & Detection"
        private val faceGestureDrawableMap = mapOf(
            "null" to R.drawable.img_not_set,
            "Brow Down Left" to R.drawable.img_brow_down_left,
            "Brow Down Right" to R.drawable.img_brow_down_right,
            "Brow Inner Up" to R.drawable.img_brow_inner_up,
            "Brow Outer Up Left" to R.drawable.img_brow_outer_up_left,
            "Brow Outer Up Right" to R.drawable.img_brow_outer_up_right,
            "Cheek Puff" to R.drawable.img_cheek_puff,
            "Cheek Squint Left" to R.drawable.img_cheek_squint_left,
            "Cheek Squint Right" to R.drawable.img_cheek_squint_right,
            "Eye Blink Left" to R.drawable.img_eye_blink_left,
            "Eye Blink Right" to R.drawable.img_eye_blink_right,
            "Eye Look Down Left" to R.drawable.img_eye_look_down_left,
            "Eye Look Down Right" to R.drawable.img_eye_look_down_right,
            "Eye Look In Left" to R.drawable.img_eye_lookin_left,
            "Eye Look In Right" to R.drawable.img_eye_lookin_right,
            "Eye Look Out Left" to R.drawable.img_eye_look_out_left,
            "Eye Look Out Right" to R.drawable.img_eye_look_out_right,
            "Eye Look Up Left" to R.drawable.img_eye_look_up_left,
            "Eye Look Up Right" to R.drawable.img_eye_look_up_right,
            "Eye Squint Left" to R.drawable.img_eye_squint_left,
            "Eye Squint Right" to R.drawable.img_eye_squint_right,
            "Eye Wide Left" to R.drawable.img_eye_wide_left,
            "Eye Wide Right" to R.drawable.img_eye_wide_right,
            "Jaw Forward" to R.drawable.img_jaw_forward,
            "Jaw Left" to R.drawable.img_jaw_left,
            "Jaw Open" to R.drawable.img_jaw_open,
            "Jaw Right" to R.drawable.img_jaw_right,
            "Mouth Close" to R.drawable.img_mouth_close,
            "Mouth Dimple Left" to R.drawable.img_mouth_dimple_left,
            "Mouth Dimple Right" to R.drawable.img_mouth_dimple_right,
            "Mouth Frown Left" to R.drawable.img_mouth_frown_left,
            "Mouth Frown Right" to R.drawable.img_mouth_frown_right,
            "Mouth Funnel" to R.drawable.img_mouth_funnel,
            "Mouth Left" to R.drawable.img_mouth_left,
            "Mouth Lower Down Left" to R.drawable.img_mouth_lower_down_left,
            "Mouth Lower Down Right" to R.drawable.img_mouth_lower_down_right,
            "Mouth Press Left" to R.drawable.img_mouth_press_left,
            "Mouth Press Right" to R.drawable.img_mouth_press_right,
            "Mouth Pucker" to R.drawable.img_mouth_pucker,
            "Mouth Right" to R.drawable.img_mouth_right,
            "Mouth Roll Lower" to R.drawable.img_mouth_roll_lower,
            "Mouth Roll Upper" to R.drawable.img_mouth_roll_upper,
            "Mouth Shrug Lower" to R.drawable.img_mouth_shrug_lower,
            "Mouth Shrug Upper" to R.drawable.img_mouth_shrug_upper,
            "Mouth Smile Left" to R.drawable.img_mouth_smile_left,
            "Mouth Smile Right" to R.drawable.img_mouth_smile_right,
            "Mouth Stretch Left" to R.drawable.img_mouth_stretch_left,
            "Mouth Stretch Right" to R.drawable.img_mouth_stretch_right,
            "Mouth Upper Up Left" to R.drawable.img_mouth_upper_up_left,
            "Mouth Upper Up Right" to R.drawable.img_mouth_upper_up_right,
            "Nose Sneer Left" to R.drawable.img_nose_sneer_left,
            "Nose Sneer Right" to R.drawable.img_nose_sneer_right,
        )
    }
}