package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.sidharth.lg_motion.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val username = findPreference<EditTextPreference>("username")
        val password = findPreference<EditTextPreference>("password")
        val ip = findPreference<EditTextPreference>("ip")
        val port = findPreference<EditTextPreference>("port")
        val totalScreens = findPreference<SeekBarPreference>("screens")
        val autoConnect = findPreference<SwitchPreferenceCompat>("auto_connect")
        val clearKml = findPreference<Preference>("clear_kml")
        val setRefresh = findPreference<Preference>("set_refresh")
        val resetRefresh = findPreference<Preference>("reset_refresh")
        val relaunch = findPreference<Preference>("relaunch")
        val restart = findPreference<Preference>("restart")
        val shutdown = findPreference<Preference>("shutdown")
        val mapStyle = findPreference<ListPreference>("map_style")
        val about = findPreference<Preference>("about")
        val openSourceLicense = findPreference<Preference>("opensource_license")
        val privacyPolicy = findPreference<Preference>("privacy_policy")
        val appVersion = findPreference<Preference>("app_version")

        username?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }
        }

        password?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        ip?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }
        }

        port?.apply {
            setOnBindEditTextListener { editText ->
                editText.isSingleLine = true
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }
}