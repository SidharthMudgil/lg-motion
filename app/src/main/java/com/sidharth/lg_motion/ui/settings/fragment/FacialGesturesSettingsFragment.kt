package com.sidharth.lg_motion.ui.settings.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.sidharth.lg_motion.R

class FacialGesturesSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.facial_gestures_preferences, rootKey)
    }
}