/*
Handles the camera view model
function for taking the photo and  clearing the bitmaps

 */
package com.officialsunil.pdpapplication.utils

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel : ViewModel() {
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    fun onTakePhoto(bitmap : Bitmap) {
        _bitmaps.value += bitmap

    }

    fun clearBitmaps() {
        _bitmaps.value = emptyList()
    }
}