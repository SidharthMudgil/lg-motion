package com.sidharth.lg_motion

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.sidharth.lg_motion.databinding.ActivityMainBinding
import com.sidharth.lg_motion.domain.callback.ProgressIndicatorCallback
import com.sidharth.lg_motion.ui.home.viewmodel.ProgressViewModel
import com.sidharth.lg_motion.ui.home.viewmodel.ProgressViewModelFactory
import com.sidharth.lg_motion.util.LiquidGalaxyManager
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.NotificationUtils
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), ProgressIndicatorCallback {
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val viewModel: ProgressViewModel by viewModels {
        ProgressViewModelFactory()
    }
    private val networkConnected: Boolean get() = NetworkUtils.isNetworkConnected(this)
    private var navHostFragment: Fragment? = null
    private var scheduledExecutorService: ScheduledExecutorService? = null
    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.stopNetworkCallback(this)
        scheduledExecutorService?.shutdown()
        scheduledExecutorService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(activityMainBinding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        setupBottomNavigation()
        setupRailViewNavigation()
        viewModel.connecting.observe(this) { connecting ->
            if (connecting) {
                showProgressIndicator()
            } else {
                hideProgressIndicator()
            }
        }
        viewModel.first.observe(this) { first ->
            if (first.not()) {
                setupLiquidGalaxyConnection().also {
                    viewModel.setFirstValue(true)
                    addNetworkCallbackAndStartSchedule()
                }
            }
        }
    }

    private fun addNetworkCallbackAndStartSchedule() {
        NetworkUtils.startNetworkCallback(this, onConnectionAvailable = {
            setupLiquidGalaxyConnection()
        }, onConnectionLost = {
            lifecycleScope.launch {
                LiquidGalaxyManager.getInstance()?.disconnect()
                preferences.edit().putBoolean("connection_status", false).apply()
            }
        })

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        scheduledExecutorService?.scheduleAtFixedRate({
            preferences.edit().putBoolean(
                "connection_status", LiquidGalaxyManager.getInstance()?.connected == true
            ).apply()
        }, 0, 1, TimeUnit.SECONDS)
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
            R.id.cameraFragment, R.id.audioFragment, R.id.infoFragment -> {
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

    private fun setupLiquidGalaxyConnection() {
        val editor = preferences.edit()

        val autoConnect = preferences.getBoolean("auto_connect", false)
        val username = preferences.getString("username", "") ?: ""
        val password = preferences.getString("password", "") ?: ""
        val ip = preferences.getString("ip", "") ?: ""
        val port = preferences.getString("port", "-1")?.toIntOrNull() ?: -1
        val screens = preferences.getInt("screens", 3)

        if (autoConnect && username.isNotBlank() && password.isNotBlank() && ip.isNotBlank() && port >= 0) {
            LiquidGalaxyManager.newInstance(
                username = username,
                password = password,
                host = ip,
                port = port,
                screens = screens,
            )

            if (networkConnected) {
                lifecycleScope.launch {
                    showProgressIndicator()
                    viewModel.setConnecting(true)
                    when (LiquidGalaxyManager.getInstance()?.connect()) {
                        true -> {
                            showSnackbar("Connection Successful")
                            editor.putBoolean("connection_status", true).apply()
                        }

                        else -> {
                            showSnackbar("Connection Failed")
                            editor.putBoolean("connection_status", false).apply()
                        }
                    }
                    viewModel.setConnecting(false)
                    hideProgressIndicator()
                }
            } else {
                showSnackbar("No Internet Connection")
                editor.putBoolean("connection_status", false).apply()
            }
        }
    }

    override fun showProgressIndicator() {
        runOnUiThread {
            activityMainBinding.progressIndicator.visibility = View.VISIBLE
        }
    }

    override fun hideProgressIndicator() {
        runOnUiThread {
            activityMainBinding.progressIndicator.visibility = View.GONE
        }
    }

    override fun showSnackbar(message: String) {
        runOnUiThread {
            NotificationUtils.showSnackbar(activityMainBinding.mainLayout, message, activityMainBinding.bottomNavigationView)
        }
    }
}