package com.sidharth.lg_motion.ui.home.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.carousel.CarouselLayoutManager
import com.sidharth.lg_motion.databinding.FragmentHomeBinding
import com.sidharth.lg_motion.domain.callback.OnFeatureClickCallback
import com.sidharth.lg_motion.domain.callback.OnFunActivityClickCallback
import com.sidharth.lg_motion.domain.callback.ProgressIndicatorCallback
import com.sidharth.lg_motion.domain.model.Feature
import com.sidharth.lg_motion.domain.model.FunActivity
import com.sidharth.lg_motion.ui.home.adapter.FeaturesListAdapter
import com.sidharth.lg_motion.ui.home.adapter.FunActivitiesAdapter
import com.sidharth.lg_motion.ui.home.viewmodel.ProgressViewModel
import com.sidharth.lg_motion.ui.home.viewmodel.ProgressViewModelFactory
import com.sidharth.lg_motion.util.Constants
import com.sidharth.lg_motion.util.DialogUtils
import com.sidharth.lg_motion.util.LiquidGalaxyManager
import com.sidharth.lg_motion.util.NetworkUtils
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnFunActivityClickCallback, OnFeatureClickCallback {
    private var action: NavDirections? = null
    private val viewModel: ProgressViewModel by activityViewModels {
        ProgressViewModelFactory()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (NetworkUtils.isNetworkConnected(requireContext())) {
                if (LiquidGalaxyManager.getInstance()?.connected == true) {
                    action?.let {
                        view?.findNavController()?.navigate(it)
                    }
                } else {
                    showSnackbar("No LG Connection")
                }
            } else {
                showSnackbar("No Internet Connection")
            }
        } else {
            showSnackbar("Feature requires permission")
        }
    }

    override fun onPause() {
        super.onPause()
        DialogUtils.dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)

        binding.activitiesRv.layoutManager = CarouselLayoutManager()
        binding.activitiesRv.adapter = FunActivitiesAdapter(
            context = requireContext(),
            activities = Constants.funActivities,
            onFunActivityClickCallback = this
        )
        LinearSnapHelper().attachToRecyclerView(binding.activitiesRv)

        binding.featuresRv.adapter = FeaturesListAdapter(
            context = requireContext(),
            features = Constants.featureList,
            onFeatureClickCallback = this
        )

        return binding.root
    }

    override fun onFeatureClick(type: Feature.Type) {
        val permission = when (type) {
            Feature.Type.VOICE -> Manifest.permission.RECORD_AUDIO
            else -> Manifest.permission.CAMERA
        }

        action = when (type) {
            Feature.Type.VOICE -> HomeFragmentDirections.actionHomeFragmentToAudioFragment()
            else -> HomeFragmentDirections.actionHomeFragmentToCameraFragment(feature = type.name)
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                if (NetworkUtils.isNetworkConnected(requireContext())) {
                    if (LiquidGalaxyManager.getInstance()?.connected == true) {
                        action?.let {
                            view?.findNavController()?.navigate(it)
                        }
                    } else {
                        context?.getSystemService(WindowManager::class.java)?.currentWindowMetrics?.bounds?.height()
                            ?.let { height ->
                                activity?.display?.rotation?.let {
                                    if (height >= 600 && it == Surface.ROTATION_0) {
                                        DialogUtils.show(requireContext()) {
                                            connect()
                                        }
                                    } else showSnackbar("No LG Connection")
                                } ?: showSnackbar("No LG Connection")
                            } ?: showSnackbar("No LG Connection")
                    }
                } else {
                    showSnackbar("No Internet Connection")
                }
            }

            shouldShowRequestPermissionRationale(permission) -> {
                showSnackbar("Feature requires permission")
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun connect() {
        lifecycleScope.launch {
            viewModel.setConnecting(true)
            when (LiquidGalaxyManager.getInstance()?.connect()) {
                true -> showSnackbar("Connection Successful")
                else -> showSnackbar("Connection Failed")
            }
            viewModel.setConnecting(false)
        }
    }

    override fun onFunActivityClick(name: FunActivity.Activity) {
        showSnackbar("Coming Soon")
    }

    private fun showSnackbar(message: String) {
        if (activity is ProgressIndicatorCallback) {
            (activity as ProgressIndicatorCallback?)?.showSnackbar(message)
        }
    }
}