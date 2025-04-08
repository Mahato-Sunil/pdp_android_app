/*
This is the data class that actually used to work with image
data for making the prediction

 */
package com.officialsunil.pdpapplication.data

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.officialsunil.pdpapplication.model.Classification
import com.officialsunil.pdpapplication.model.ModelClassifier
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions

class PdpModelClassifier(
    // declare the variables
    private val context: Context,
    private val threshold: Float = 0.5f,   // base for including the classiffication. take only the value greater than defined value
    private val maxResults: Int = 1   // number of prediction to make
) : ModelClassifier {

    private var classifier: ImageClassifier? = null

    // set the classifier
    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(threshold)
            .build()

        try {
            // dont use named parameters here
            // its written in java so it doesn't support yet

            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark.tflite",
//                "food.tflite",
                options,

                )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    // override the methods
    override fun classify(
        bitmap: Bitmap,
        rotation: Int
    ): List<Classification> {

        if (classifier == null)
            setupClassifier()

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage =
            imageProcessor.process(TensorImage.fromBitmap(bitmap))    // convert the bitmap to tensor image
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()
        val results = classifier?.classify(tensorImage, imageProcessingOptions)

        /*
        flatten the classification to get the results
        return the class name (display names) and the model accuracy (score)

        if the prediction is done it returns the name and the score
        else
        returns the empty list

        distinc by ==> it check if there is duplicate and removes them and return single value
         */
        return results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    name = category.displayName,
                    score = category.score
                )
            }
        }?.distinctBy { it.name } ?: emptyList()
    }

    // get the orientation of the image
    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }
}