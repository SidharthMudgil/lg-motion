package com.sidharth.lg_motion.ui.settings.fragment

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.domain.callback.ProgressIndicatorCallback
import com.sidharth.lg_motion.ui.home.viewmodel.ProgressViewModel
import com.sidharth.lg_motion.ui.home.viewmodel.ProgressViewModelFactory
import com.sidharth.lg_motion.ui.settings.preference.ConnectionStatusPreference
import com.sidharth.lg_motion.util.LiquidGalaxyManager
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.RangeInputFilter
import com.sidharth.lg_motion.util.TextUtils
import kotlinx.coroutines.launch


class SettingsFragment : PreferenceFragmentCompat() {
    private val connectionStatusPreference by lazy { findPreference<ConnectionStatusPreference>("connection_status")!! }
    private val usernamePreference by lazy { findPreference<EditTextPreference>("username")!! }
    private val passwordPreference by lazy { findPreference<EditTextPreference>("password")!! }
    private val ipPreference by lazy { findPreference<EditTextPreference>("ip")!! }
    private val portPreference by lazy { findPreference<EditTextPreference>("port")!! }
    private val totalScreensPreference by lazy { findPreference<SeekBarPreference>("screens")!! }
    private val autoConnectPreference by lazy { findPreference<SwitchPreferenceCompat>("auto_connect")!! }
    private val connectPreference by lazy { findPreference<Preference>("connect")!! }
    private val clearKmlPreference by lazy { findPreference<Preference>("clear_kml")!! }
    private val setRefreshPreference by lazy { findPreference<Preference>("set_refresh")!! }
    private val resetRefreshPreference by lazy { findPreference<Preference>("reset_refresh")!! }
    private val relaunchPreference by lazy { findPreference<Preference>("relaunch")!! }
    private val restartPreference by lazy { findPreference<Preference>("restart")!! }
    private val shutdownPreference by lazy { findPreference<Preference>("shutdown")!! }
    private val aboutPreference by lazy { findPreference<Preference>("about")!! }
    private val openSourceLicensePreference by lazy { findPreference<Preference>("opensource_license")!! }
    private val privacyPolicyPreference by lazy { findPreference<Preference>("privacy_policy")!! }
    private val appVersionPreference by lazy { findPreference<Preference>("app_version")!! }

    private val viewModel: ProgressViewModel by activityViewModels {
        ProgressViewModelFactory()
    }
    private val networkConnected: Boolean
        get() {
            return NetworkUtils.isNetworkConnected(requireContext())
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        if (connectionStatusPreference.isConnected()) {
            onConnection(showSnackbar = false)
        } else {
            onDisconnection(showSnackbar = false)
        }

        setupChangeListeners()
        setupClickListeners()
    }

    private fun setupChangeListeners() {
        val inputFilter = InputFilter.LengthFilter(30)

        usernamePreference.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_TEXT
                editText.filters = arrayOf(inputFilter)
                editText.hint = "lg"
                editText.setSelection(editText.text.length)
            }
        }

        passwordPreference.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                editText.filters = arrayOf(inputFilter)
                editText.hint = "lg"
                editText.setSelection(editText.text.length)
            }
        }

        ipPreference.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_TEXT
                editText.filters = arrayOf(inputFilter)
                editText.hint = "127.0.0.1"
                editText.setSelection(editText.text.length)
            }
        }

        portPreference.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf(RangeInputFilter(max = 65536))
                editText.hint = "22"
                editText.setSelection(editText.text.length)
            }
        }

        usernamePreference.setOnPreferenceChangeListener { _, newValue ->
            val isValidInput = (newValue as String).isNotBlank()
            if (isValidInput && autoConnectPreference.isChecked) {
                usernamePreference.text = newValue
                connect()
            }
            isValidInput
        }

        passwordPreference.setOnPreferenceChangeListener { _, newValue ->
            val isValidInput = (newValue as String).isNotBlank()
            if (isValidInput && autoConnectPreference.isChecked) {
                passwordPreference.text = newValue
                connect()
            }
            isValidInput
        }

        portPreference.setOnPreferenceChangeListener { _, newValue ->
            val isValidInput = (newValue as String).isNotBlank()
            if (isValidInput && autoConnectPreference.isChecked) {
                portPreference.text = newValue
                connect()
            }
            isValidInput
        }

        ipPreference.setOnPreferenceChangeListener { _, newValue ->
            val isValidIp = TextUtils.isValidIp(newValue as String)
            if (isValidIp && autoConnectPreference.isChecked) {
                ipPreference.text = newValue
                connect()
            }
            isValidIp
        }

        totalScreensPreference.setOnPreferenceChangeListener { _, _ ->
            if (autoConnectPreference.isChecked) {
                connect()
            }
            true
        }
    }

    private fun setupClickListeners() {
        connectPreference.setOnPreferenceClickListener {
            if (connectionStatusPreference.isConnected()) {
                disconnect()
            } else {
                connect(true)
            }
            true
        }

        setRefreshPreference.setOnPreferenceClickListener {
            execute(Action.SET_REFRESH)
            true
        }

        resetRefreshPreference.setOnPreferenceClickListener {
            execute(Action.RESET_REFRESH)
            true
        }

        clearKmlPreference.setOnPreferenceClickListener {
            execute(Action.CLEAR_KML)
            true
        }

        relaunchPreference.setOnPreferenceClickListener {
            execute(Action.RELAUNCH)
            true
        }

        restartPreference.setOnPreferenceClickListener {
            execute(Action.RESTART)
            true
        }

        shutdownPreference.setOnPreferenceClickListener {
            execute(Action.SHUTDOWN)
            true
        }

        aboutPreference.setOnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
            view?.findNavController()?.navigate(action)
            true
        }

        openSourceLicensePreference.setOnPreferenceClickListener {
            val action =
                SettingsFragmentDirections.actionSettingsFragmentToOpenSourceLicenseFragment()
            view?.findNavController()?.navigate(action)
            true
        }

        privacyPolicyPreference.setOnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyPolicyFragment()
            view?.findNavController()?.navigate(action)
            true
        }

        appVersionPreference.setOnPreferenceClickListener {
            showSnackbar(
                "version ${
                    requireContext().packageManager.getPackageInfo(
                        requireContext().packageName,
                        0
                    ).versionName
                }"
            )
            true
        }
    }

    private fun execute(action: Action) {
        if (networkConnected) {
            if (LiquidGalaxyManager.getInstance()?.connected == true) {
                lifecycleScope.launch {
                    viewModel.setConnecting(true)
                    LiquidGalaxyManager.getInstance()?.apply {
                        when (action) {
                            Action.CLEAR_KML -> clearKml()
                            Action.SET_REFRESH -> setRefresh()
                            Action.RESET_REFRESH -> resetRefresh()
                            Action.RELAUNCH -> relaunch()
                            Action.RESTART -> restart()
                            Action.SHUTDOWN -> shutdown()
                        }
                        viewModel.setConnecting(false)
                    }
                }
            } else {
                showSnackbar("No LG Connection")
            }
        } else {
            showSnackbar("No Internet Connection")
        }
    }

    private fun connect(forceConnect: Boolean = false) {
        val username = usernamePreference.text ?: ""
        val password = passwordPreference.text ?: ""
        val host = ipPreference.text ?: ""
        val port = portPreference.text ?: ""
        val screens = totalScreensPreference.value

        if (username.isNotBlank() && password.isNotBlank() && TextUtils.isValidIp(host) && port.isNotBlank()) {
            disconnect(showSnackbar = false)
            LiquidGalaxyManager.newInstance(
                username = username,
                password = password,
                host = host,
                port = port.toInt(),
                screens = screens,
            )
            if (forceConnect || autoConnectPreference.isChecked) {
                if (networkConnected) {
                    lifecycleScope.launch {
                        viewModel.setConnecting(true)
                        when (LiquidGalaxyManager.getInstance()?.connect()) {
                            true -> onConnection()
                            else -> onDisconnection(updatePreference = false)
                        }
                        viewModel.setConnecting(false)
                    }
                } else {
                    onDisconnection("No Internet Connection", updatePreference = false)
                }
            }
        } else if (forceConnect) {
            onDisconnection(message = "Invalid Connection Info", updatePreference = false)
        }
    }

    private fun disconnect(showSnackbar: Boolean = true) {
        if (LiquidGalaxyManager.getInstance() != null) {
            lifecycleScope.launch {
                LiquidGalaxyManager.getInstance()?.disconnect()
                onDisconnection("Disconnected", showSnackbar)
            }
        }
    }

    private fun onConnection(showSnackbar: Boolean = true) {
        if (isAdded) {
            connectPreference.title = requireContext().getString(R.string.disconnect)
            connectPreference.summary = requireContext().getString(R.string.disconnect_summary)
            connectionStatusPreference.setConnectionStatus(true)
        }

        if (showSnackbar) {
            showSnackbar("Connection Successful")
        }
    }

    private fun onDisconnection(message: String = "Connection Failed", showSnackbar: Boolean = true, updatePreference: Boolean = true) {
        if (isAdded && updatePreference) {
            connectPreference.title = requireContext().getString(R.string.connect)
            connectPreference.summary = requireContext().getString(R.string.connect_summary)
            connectionStatusPreference.setConnectionStatus(false)
        }

        if (showSnackbar) {
            showSnackbar(message)
        }
    }

    private fun showSnackbar(message: String) {
        if (activity is ProgressIndicatorCallback) {
            (activity as ProgressIndicatorCallback?)?.showSnackbar(message)
        }
    }

    companion object {
        private enum class Action {
            CLEAR_KML, SET_REFRESH, RESET_REFRESH, RELAUNCH, RESTART, SHUTDOWN
        }
    }
}