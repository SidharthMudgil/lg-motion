package com.sidharth.lg_motion.ui.view.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.sidharth.lg_motion.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}