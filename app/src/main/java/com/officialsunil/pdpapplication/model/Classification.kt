package com.officialsunil.pdpapplication.model

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.officialsunil.pdpapplication.utils.centerCrop

data class Classification (
    val name : String,
    val score : Float
)

// this class is responsible for analyzing the
// image frame by frame
class PotatoDiseaseAnalyzer(
    private val classifier: ModelClassifier,
    private val onResult: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0    // for skipping the frames to improve the performance

    //overrie the analyzer
    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter % 60 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                .centerCrop(224, 224)   // change based on themodel requirements

            val results = classifier.classify(bitmap, rotationDegrees)
            onResult(results)
        }
        frameSkipCounter++
        image.close()
    }
}