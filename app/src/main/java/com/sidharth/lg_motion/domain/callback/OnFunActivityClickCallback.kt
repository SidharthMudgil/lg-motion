package com.sidharth.lg_motion.domain.callback

import com.sidharth.lg_motion.domain.model.FunActivity

interface OnFunActivityClickCallback {
    fun onFunActivityClick(name: FunActivity.Activity)
}