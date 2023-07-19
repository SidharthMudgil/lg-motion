package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.carousel.CarouselLayoutManager
import com.sidharth.lg_motion.databinding.FragmentHomeBinding
import com.sidharth.lg_motion.domain.callback.OnFeatureClickCallback
import com.sidharth.lg_motion.domain.callback.OnFunActivityClickCallback
import com.sidharth.lg_motion.domain.model.Feature
import com.sidharth.lg_motion.domain.model.FunActivity
import com.sidharth.lg_motion.ui.view.adapter.FeaturesListAdapter
import com.sidharth.lg_motion.ui.view.adapter.FunActivitiesAdapter
import com.sidharth.lg_motion.util.Constants

class HomeFragment : Fragment(), OnFunActivityClickCallback, OnFeatureClickCallback {

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

        binding.featuresRv.layoutManager = LinearLayoutManager(requireContext())
        binding.featuresRv.adapter = FeaturesListAdapter(
            context = requireContext(),
            features = Constants.featureList,
            onFeatureClickCallback = this
        )

        return binding.root
    }

    override fun onFeatureClick(type: Feature.Type) {
        val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment()
        view?.findNavController()?.navigate(action)
        when (type) {
            Feature.Type.FACE -> {

            }

            Feature.Type.HAND -> {

            }

            Feature.Type.VOICE -> {

            }

            Feature.Type.POSE -> {

            }

            Feature.Type.OBJECT -> {

            }
        }
    }

    override fun onFunActivityClick(name: String) {
        when (name) {
            FunActivity.Activity.ASTEROIDS.name -> {

            }

            FunActivity.Activity.SNAKE.name -> {

            }

            FunActivity.Activity.PACMAN.name -> {

            }

            FunActivity.Activity.TETRIS.name -> {

            }

            FunActivity.Activity.MARIO.name -> {

            }

            FunActivity.Activity.FACE_MASK.name -> {

            }

            FunActivity.Activity.DINO_RUN.name -> {

            }
        }
        val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment()
        view?.findNavController()?.navigate(action)
    }
}