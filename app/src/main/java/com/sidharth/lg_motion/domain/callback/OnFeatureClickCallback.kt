package com.sidharth.lg_motion.domain.callback

import com.sidharth.lg_motion.domain.model.Feature

interface OnFeatureClickCallback {
    fun onFeatureClick(type: Feature.Type)
}