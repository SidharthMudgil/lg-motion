package com.sidharth.lg_motion.ui.settings.fragment

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.sidharth.lg_motion.R

class FacialGesturesSettingsFragment : PreferenceFragmentCompat(), OnPreferenceChangeListener {

    private val moveNorthGesture: ListPreference by lazy { findPreference("move_north_gesture")!! }
    private val moveSouthGesture: ListPreference by lazy { findPreference("move_south_gesture")!! }
    private val moveEastGesture: ListPreference by lazy { findPreference("move_east_gesture")!! }
    private val moveWestGesture: ListPreference by lazy { findPreference("move_west_gesture")!! }
    private val rotateLeftGesture: ListPreference by lazy { findPreference("rotate_left_gesture")!! }
    private val rotateRightGesture: ListPreference by lazy { findPreference("rotate_right_gesture")!! }
    private val zoomInGesture: ListPreference by lazy { findPreference("zoom_in_gesture")!! }
    private val zoomOutGesture: ListPreference by lazy { findPreference("zoom_out_gesture")!! }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.facial_gestures_preferences, rootKey)
        setupChangeListeners()
    }

    private fun setupChangeListeners() {
        moveNorthGesture.onPreferenceChangeListener = this
        moveSouthGesture.onPreferenceChangeListener = this
        moveEastGesture.onPreferenceChangeListener = this
        moveWestGesture.onPreferenceChangeListener = this
        rotateLeftGesture.onPreferenceChangeListener = this
        rotateRightGesture.onPreferenceChangeListener = this
        zoomInGesture.onPreferenceChangeListener = this
        zoomOutGesture.onPreferenceChangeListener = this
    }

    private fun handleGestureSelection(gesture: String): Boolean {
        val selectedGestures = listOf(
            moveNorthGesture.value,
            moveSouthGesture.value,
            moveEastGesture.value,
            moveWestGesture.value,
            rotateLeftGesture.value,
            rotateRightGesture.value,
            zoomInGesture.value,
            zoomOutGesture.value,
        )

        return selectedGestures.contains(gesture).not()
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        return handleGestureSelection(newValue as String)
    }
}