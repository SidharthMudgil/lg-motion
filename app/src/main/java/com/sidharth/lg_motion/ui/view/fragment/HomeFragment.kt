package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.material.carousel.CarouselLayoutManager
import com.sidharth.lg_motion.databinding.FragmentHomeBinding
import com.sidharth.lg_motion.ui.view.adapter.FeaturesListAdapter
import com.sidharth.lg_motion.ui.view.adapter.FunActivitiesAdapter
import com.sidharth.lg_motion.util.Constants

class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)

        binding.activitiesRv.layoutManager = CarouselLayoutManager()
        binding.activitiesRv.adapter = FunActivitiesAdapter(requireContext(), Constants.funActivities)
        LinearSnapHelper().attachToRecyclerView(binding.activitiesRv)

        binding.featuresRv.layoutManager = LinearLayoutManager(requireContext())
        binding.featuresRv.adapter = FeaturesListAdapter(requireContext(), Constants.featureList)

        return binding.root
    }

}