package com.officialsunil.pdpapplication.model

import android.graphics.Bitmap

interface ModelClassifier {
    // declare the function that is used for classifying the image data
    fun classify(bitmap: Bitmap, rotation: Int): List<Classification>
}