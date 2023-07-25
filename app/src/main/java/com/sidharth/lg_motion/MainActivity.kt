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
            this.addOnDestinationChangedListener { _, destination, _ ->
                checkBottomNavigationViewMenuItem(destination.id)
            }
        }
    }

    private fun setupRailViewNavigation() {
        navHostFragment?.findNavController()?.apply {
            activityMainBinding.navigationRailView?.setupWithNavController(this)
            this.addOnDestinationChangedListener { _, destination, _ ->
                checkBottomNavigationViewMenuItem(destination.id)
            }
        }
    }

    private fun checkBottomNavigationViewMenuItem(id: Int) {
        when (id) {
            R.id.cameraFragment, R.id.audioFragment -> {
                activityMainBinding.bottomNavigationView?.menu?.findItem(R.id.homeFragment)?.isChecked =
                    true
                activityMainBinding.navigationRailView?.menu?.findItem(R.id.homeFragment)?.isChecked =
                    true
            }

            R.id.aboutFragment, R.id.openSourceLicenseFragment, R.id.privacyPolicyFragment -> {
                activityMainBinding.bottomNavigationView?.menu?.findItem(R.id.settingsFragment)?.isChecked =
                    true
                activityMainBinding.navigationRailView?.menu?.findItem(R.id.settingsFragment)?.isChecked =
                    true
            }
        }
    }
}