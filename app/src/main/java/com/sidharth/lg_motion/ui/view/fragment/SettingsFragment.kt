package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.navigation.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.sidharth.lg_motion.R
import com.sidharth.lg_motion.util.RangeInputFilter
import com.sidharth.lg_motion.util.ToastUtil

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val usernamePreference = findPreference<EditTextPreference>("username")
        val passwordPreference = findPreference<EditTextPreference>("password")
        val ipPreference = findPreference<EditTextPreference>("ip")
        val portPreference = findPreference<EditTextPreference>("port")
        val totalScreensPreference = findPreference<SeekBarPreference>("screens")
        val autoConnectPreference = findPreference<SwitchPreferenceCompat>("auto_connect")
        val clearKmlPreference = findPreference<Preference>("clear_kml")
        val setRefreshPreference = findPreference<Preference>("set_refresh")
        val resetRefreshPreference = findPreference<Preference>("reset_refresh")
        val relaunchPreference = findPreference<Preference>("relaunch")
        val restartPreference = findPreference<Preference>("restart")
        val shutdownPreference = findPreference<Preference>("shutdown")
        val mapStylePreference = findPreference<ListPreference>("map_style")
        val aboutPreference = findPreference<Preference>("about")
        val openSourceLicensePreference = findPreference<Preference>("opensource_license")
        val privacyPolicyPreference = findPreference<Preference>("privacy_policy")
        val appVersionPreference = findPreference<Preference>("app_version")

        val inputFilter = InputFilter.LengthFilter(30)

        usernamePreference?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_TEXT
                editText.filters = arrayOf(inputFilter)
                editText.hint = "lg"
                editText.setSelection(editText.text.length)
            }
        }

        passwordPreference?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                editText.filters = arrayOf(inputFilter)
                editText.hint = "lg"
                editText.setSelection(editText.text.length)
            }
        }

        ipPreference?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_TEXT
                editText.filters = arrayOf(inputFilter)
                editText.hint = "127.0.0.1"
                editText.setSelection(editText.text.length)
            }
        }

        portPreference?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.filters = arrayOf(RangeInputFilter(max = 65536))
                editText.hint = "22"
                editText.setSelection(editText.text.length)
            }
        }

        appVersionPreference?.setOnPreferenceClickListener {
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

        aboutPreference?.setOnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()
            view?.findNavController()?.navigate(action)
            true
        }

        openSourceLicensePreference?.setOnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToOpenSourceLicenseFragment()
            view?.findNavController()?.navigate(action)
            true
        }

        privacyPolicyPreference?.setOnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToPrivacyPolicyFragment()
            view?.findNavController()?.navigate(action)
            true
        }
    }
}