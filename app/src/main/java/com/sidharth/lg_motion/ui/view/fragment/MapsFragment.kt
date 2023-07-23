package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.sidharth.lg_motion.R

class MapsFragment : Fragment() {
    private val mapsFragmentArgs: MapsFragmentArgs by navArgs()
    private var mapStyle: Int = R.raw.map_style_blue
    private var mapType: Int = GoogleMap.MAP_TYPE_NORMAL

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.mapType = mapType
        updateMapStyle(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapStyle = when (mapsFragmentArgs.style) {
            "Standard" -> R.raw.map_style_standard
            "Dark" -> R.raw.map_style_dark
            "Retro" -> R.raw.map_style_retro
            "Blue" -> R.raw.map_style_blue
            "Green" -> R.raw.map_style_green
            "Night" -> R.raw.map_style_night
            "Silver" -> R.raw.map_style_silver
            else -> R.raw.map_style_blue
        }
        mapType = when (mapsFragmentArgs.style) {
            "Satellite" -> GoogleMap.MAP_TYPE_HYBRID
            else -> GoogleMap.MAP_TYPE_NORMAL
        }

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun updateMapStyle(googleMap: GoogleMap) {
        googleMap.setMapStyle(context?.let {
            MapStyleOptions.loadRawResourceStyle(
                it, mapStyle
            )
        })
    }
}