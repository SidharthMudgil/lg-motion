package com.sidharth.lg_motion.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProgressViewModel : ViewModel() {
    private val _first = MutableLiveData<Boolean>()
    val first: LiveData<Boolean> get() = _first

    private val _connecting = MutableLiveData<Boolean>()
    val connecting: LiveData<Boolean> get() = _connecting

    init {
        _first.value = false
        _connecting.value = false
    }

    fun setFirstValue(value: Boolean) {
        _first.postValue(value)
    }

    fun setConnecting(value: Boolean) {
        _connecting.postValue(value)
    }
}