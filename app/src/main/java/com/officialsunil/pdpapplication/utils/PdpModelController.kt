package com.officialsunil.pdpapplication.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PdpModelController : ViewModel() {

    // use this model class to load the model in the system


    // for demo
    private val _isModelReady = MutableStateFlow(false)
    val isModelReady = _isModelReady.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000L)
            _isModelReady.value = true
        }
    }
}