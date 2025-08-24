package com.officialsunil.pdpapplication.tfLiteModule


import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.officialsunil.pdpapplication.utils.Classification
import com.officialsunil.pdpapplication.utils.centerCrop

class TFLiteModelAnalyzer(
    private val classifier: LiteRtClassifier, private val onResult: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0    // for skipping the frames to improve the performance

    //override the analyzer
    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter % 60 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap =
                image.toBitmap().centerCrop(224, 224)   // change based on the model requirements

            val results = classifier.getModelPrediction(bitmap, rotationDegrees)
            onResult(results)
        }
        frameSkipCounter++
        image.close()
    }
}

