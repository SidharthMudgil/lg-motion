package com.sidharth.lg_motion.ui.home.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.carousel.CarouselLayoutManager
import com.sidharth.lg_motion.databinding.FragmentHomeBinding
import com.sidharth.lg_motion.domain.callback.OnFeatureClickCallback
import com.sidharth.lg_motion.domain.callback.OnFunActivityClickCallback
import com.sidharth.lg_motion.domain.model.Feature
import com.sidharth.lg_motion.domain.model.FunActivity
import com.sidharth.lg_motion.ui.home.adapter.FeaturesListAdapter
import com.sidharth.lg_motion.ui.home.adapter.FunActivitiesAdapter
import com.sidharth.lg_motion.util.Constants
import com.sidharth.lg_motion.util.DialogUtils
import com.sidharth.lg_motion.util.LiquidGalaxyController
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.ToastUtil
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnFunActivityClickCallback, OnFeatureClickCallback {
    private var action: NavDirections? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (NetworkUtils.isNetworkConnected(requireContext())){
                if (LiquidGalaxyController.getInstance()?.connected == true) {
                    action?.let {
                        view?.findNavController()?.navigate(it)
                    }
                } else {
                    showToast("No LG Connection")
                }
            }else {
                showToast("No Internet Connection")
            }
        } else {
            ToastUtil.showToast(requireContext(), "Feature requires permission")
        }
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
                    if (LiquidGalaxyController.getInstance()?.connected == true) {
                        action?.let {
                            view?.findNavController()?.navigate(it)
                        }
                    } else {
                        DialogUtils.show(requireContext()) {
                            if (NetworkUtils.isNetworkConnected(requireContext())) {
                                lifecycleScope.launch {
                                    when (LiquidGalaxyController.getInstance()?.connect()) {
                                        true -> showToast("Connection Successful")
                                        else -> showToast("Connection Failed")
                                    }
                                }
                            } else {
                                showToast("No Internet Connection")
                            }
                        }
                    }
                } else {
                    showToast("No Internet Connection")
                }
            }

            shouldShowRequestPermissionRationale(permission) -> {
                ToastUtil.showToast(requireContext(), "Feature requires permission")
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showToast(message: String) {
        activity?.runOnUiThread {
            ToastUtil.showToast(requireContext(), message)
        }
    }

    override fun onFunActivityClick(name: FunActivity.Activity) {
        ToastUtil.showToast(requireContext(), "Coming Soon")
    }

    override fun onPause() {
        super.onPause()
        DialogUtils.dismiss()
    }
}