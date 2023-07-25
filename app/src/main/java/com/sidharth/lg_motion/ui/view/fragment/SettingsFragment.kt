package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.util.LiquidGalaxyController
import com.sidharth.lg_motion.util.NetworkUtils
import com.sidharth.lg_motion.util.RangeInputFilter
import com.sidharth.lg_motion.util.ToastUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {
    private val usernamePreference by lazy { findPreference<EditTextPreference>("username")!! }
    private val passwordPreference by lazy { findPreference<EditTextPreference>("password")!! }
    private val ipPreference by lazy { findPreference<EditTextPreference>("ip")!! }
    private val portPreference by lazy { findPreference<EditTextPreference>("port")!! }
    private val totalScreensPreference by lazy { findPreference<SeekBarPreference>("screens")!! }
    private val autoConnectPreference by lazy { findPreference<SwitchPreferenceCompat>("auto_connect")!! }
    private val clearKmlPreference by lazy { findPreference<Preference>("clear_kml")!! }
    private val setRefreshPreference by lazy { findPreference<Preference>("set_refresh")!! }
    private val resetRefreshPreference by lazy { findPreference<Preference>("reset_refresh")!! }
    private val relaunchPreference by lazy { findPreference<Preference>("relaunch")!! }
    private val restartPreference by lazy { findPreference<Preference>("restart")!! }
    private val shutdownPreference by lazy { findPreference<Preference>("shutdown")!! }
    private val mapStylePreference by lazy { findPreference<ListPreference>("map_style")!! }
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

        val preferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            val isValidInput = (newValue as String).isNotBlank()
            if (isValidInput && autoConnectPreference.isChecked) {
                connect()
            }
            isValidInput
        }
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

        usernamePreference.onPreferenceChangeListener = preferenceChangeListener

        passwordPreference.onPreferenceChangeListener = preferenceChangeListener

        portPreference.onPreferenceChangeListener = preferenceChangeListener

        ipPreference.setOnPreferenceChangeListener { _, newValue ->
            val isValidIp = (newValue as String).isValidIp()
            if (isValidIp && autoConnectPreference.isChecked) {
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

        setRefreshPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.setRefresh()
                }
            } else {
                noNetwork()
            }
            true
        }

        resetRefreshPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.resetRefresh()
                }
            } else {
                noNetwork()
            }
            true
        }

        clearKmlPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.clearKml()
                }
            } else {
                noNetwork()
            }
            true
        }

        relaunchPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.relaunch()
                }
            } else {
                noNetwork()
            }
            true
        }

        restartPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.restart()
                }
            } else {
                noNetwork()
            }
            true
        }

        shutdownPreference.setOnPreferenceClickListener {
            if (networkConnected) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.shutdown()
                }
            } else {
                noNetwork()
            }
            true
        }

        mapStylePreference.setOnPreferenceChangeListener { _, newValue ->
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

    private fun String.isValidIp(): Boolean {
        val ipPattern =
            "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$".toRegex()
        return isNotBlank() && matches(ipPattern)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun connect() {
        val username = usernamePreference.text?.trim().toString()
        val password = passwordPreference.text?.trim().toString()
        val host = ipPreference.text?.trim().toString()
        val port = portPreference.text?.trim().toString()
        val screens = totalScreensPreference.value

        if (username.isNotBlank() && password.isNotBlank() && host.isValidIp() && port.isNotBlank()) {
            if (LiquidGalaxyController.getInstance() != null) {
                lifecycleScope.launch {
                    LiquidGalaxyController.getInstance()?.disconnect()
                }
            }

            LiquidGalaxyController.updateInstance(
                username = username,
                password = password,
                host = host,
                port = port.toInt(),
                screens = screens,
            )
            if (autoConnectPreference.isChecked) {
                if (networkConnected) {
                    GlobalScope.launch {
                        LiquidGalaxyController.getInstance()?.connect()
                    }
                } else {
                    noNetwork()
                }
            }
        }
    }

    private fun noNetwork() {
        ToastUtil.showToast(requireContext(), "No Network")
    }
}