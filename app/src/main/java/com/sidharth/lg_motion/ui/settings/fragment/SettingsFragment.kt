package com.sidharth.lg_motion.ui.settings.fragment

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.ui.settings.preference.ConnectionStatusPreference
import com.sidharth.lg_motion.util.LiquidGalaxyController
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.RangeInputFilter
import com.sidharth.lg_motion.util.TextUtils
import com.sidharth.lg_motion.util.ToastUtil
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
    private val networkConnected: Boolean
        get() {
            return NetworkUtils.isNetworkConnected(requireContext())
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val inputFilter = InputFilter.LengthFilter(30)

        connectionStatusPreference.setOnPreferenceChangeListener { _, newValue ->
            connectionStatusPreference.setConnectionStatus(newValue as Boolean)
            true
        }

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

        connectPreference.setOnPreferenceClickListener {
            if (connectionStatusPreference.getConnectionStatus()) {
                disconnect()
            } else {
                connect(true)
            }
            true
        }

        setRefreshPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.setRefresh()
                }
            } else {
                showToast("No Internet Connection")
            }
            true
        }

        resetRefreshPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.resetRefresh()
                }
            } else {
                showToast("No Internet Connection")
            }
            true
        }

        clearKmlPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.clearKml()
                }
            } else {
                showToast("No Internet Connection")
            }
            true
        }

        relaunchPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.relaunch()
                }
            } else {
                showToast("No Internet Connection")
            }
            true
        }

        restartPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.restart()
                }
            } else {
                showToast("No Internet Connection")
            }
            true
        }

        shutdownPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.shutdown()
                }
            } else {
                showToast("No Internet Connection")
            }
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
            ToastUtil.showToast(
                requireContext(),
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

    private fun disconnect(showToast: Boolean = true) {
        if (LiquidGalaxyController.getInstance() != null) {
            lifecycleScope.launch {
                LiquidGalaxyController.getInstance()?.disconnect()
                connectionStatusPreference.setConnectionStatus(false)
                if (showToast) {
                    showToast("Disconnected")
                }
            }
        }
    }

    private fun connect(forceConnect: Boolean = false) {
        val username = usernamePreference.text ?: ""
        val password = passwordPreference.text ?: ""
        val host = ipPreference.text ?: ""
        val port = portPreference.text ?: ""
        val screens = totalScreensPreference.value

        if (username.isNotBlank() && password.isNotBlank() && TextUtils.isValidIp(host) && port.isNotBlank()) {
            disconnect(showToast = false)

            LiquidGalaxyController.newInstance(
                username = username,
                password = password,
                host = host,
                port = port.toInt(),
                screens = screens,
            )
            if (forceConnect || autoConnectPreference.isChecked) {
                if (networkConnected) {
                    lifecycleScope.launch {
                        when (LiquidGalaxyController.getInstance()?.connect()) {
                            true -> {
                                connectPreference.title = getString(R.string.disconnect)
                                connectPreference.summary = getString(R.string.disconnect_summary)
                                connectionStatusPreference.setConnectionStatus(true)
                                showToast("Connection Successful")
                            }

                            else -> {
                                connectPreference.title = getString(R.string.connect)
                                connectPreference.summary = getString(R.string.connect_summary)
                                connectionStatusPreference.setConnectionStatus(false)
                                showToast("Connection Failed")
                            }
                        }
                    }
                } else {
                    connectPreference.title = getString(R.string.connect)
                    connectPreference.summary = getString(R.string.connect_summary)
                    connectionStatusPreference.setConnectionStatus(false)
                    showToast("No Internet Connection")
                }
            }
        } else if (forceConnect) {
            connectionStatusPreference.setConnectionStatus(false)
            showToast("Invalid Connection Info")
        }
    }

    private fun showToast(message: String) {
        ToastUtil.showToast(requireContext(), message)
    }
}