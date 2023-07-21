package com.sidharth.lg_motion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sidharth.lg_motion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var navHostFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        setupBottomNavigation()
        setupRailViewNavigation()
    }

    private fun setupBottomNavigation() {
        navHostFragment?.findNavController()?.apply {
            activityMainBinding.bottomNavigationView?.setupWithNavController(this)
        }
    }

    private fun setupRailViewNavigation() {
        navHostFragment?.findNavController()?.apply {
            activityMainBinding.navigationRailView?.setupWithNavController(this)
        }
    }
}