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
    companion object {
        private const val TAG = "Landmarker & Detection"
    }

    private var lastState: LiquidGalaxyManager.State = LiquidGalaxyManager.State.IDLE
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }
    private val args: CameraFragmentArgs by navArgs()

    private lateinit var faceLandmarkerHelper: FaceLandmarkerHelper
//    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
//    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
//    private lateinit var objectDetectorHelper: ObjectDetectorHelper

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater)
        return fragmentCameraBinding.root
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
            val newState =
                SpeechLandmarkerResultParser.getStateFromFaceLandmarkerResult(resultBundle)
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
}