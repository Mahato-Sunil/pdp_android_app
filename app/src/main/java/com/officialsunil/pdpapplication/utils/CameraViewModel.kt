/*
Handles the camera view model
Function for taking the photo and clearing the bitmap
 */
package com.officialsunil.pdpapplication.utils

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel : ViewModel() {

    // ✅ Store only one bitmap (nullable)
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun clearBitmap() {
        _bitmap.value = null
    }

    // ✅ Predictions state
    private val _predictions = MutableStateFlow<List<Classification>>(emptyList())
    val predictions = _predictions.asStateFlow()

    fun onPrediction(results: List<Classification>) {
        _predictions.value = results
    }

    fun clearPredictions() {
        _predictions.value = emptyList()
    }

    // separating the camera and gallery mode
    enum class InputMode { CAMERA, GALLERY }

    private val _inputMode = MutableStateFlow(InputMode.CAMERA)
    val inputMode = _inputMode.asStateFlow()

    fun setInputMode(mode: InputMode) {
        _inputMode.value = mode
    }
}


