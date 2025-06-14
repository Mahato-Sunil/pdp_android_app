///*
//    This class is responsible for loading the model  and
//    performing the inference on the data
//
//    => Initializes the Interpreter
//    => classify and generate the result
//    => closes the resources
//
//    Input : context , model path, image size and gpu delegates
//    output : classification result
//
// */
//
package com.officialsunil.pdpapplication.tfLiteModule

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import com.officialsunil.pdpapplication.tfLiteModule.ImagePreprocessing.getImageByteBuffer
import com.officialsunil.pdpapplication.tfLiteModule.ImagePreprocessing.getRotatedBitmap
import com.officialsunil.pdpapplication.utils.Classification
import androidx.core.graphics.get

class LiteRtClassifier(
    context: Context,
    private val inputSize: Int = 224,   // default size of the image  to be fed to the model
    private val useGpu: Boolean = false
) {
    // declare the interpreter and gpu delegate and other informations
    private val modelName = "pdp_model.tflite"
    private val numClasses = 4
    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null

    private var TAG = "LiteRtClassifier"

    //  initialize the classifier and load the model
    init {
        val options = Interpreter.Options().apply {
            setNumThreads(4)
            if (useGpu) {
                gpuDelegate = GpuDelegate()
                this.addDelegate(gpuDelegate)
            }
        }

        // load the model using Interpreter
        val model = LoadModel.loadModelFile(context, modelName)
        interpreter = Interpreter(model, options)

        //load the label too
        LoadModel.loadLabel(context, "labels.txt")
    }

    // function to run the inference and return the result
    internal fun classify(bitmap: Bitmap, rotation: Int = 0): FloatArray {
        val rotatedBitmap = getRotatedBitmap(bitmap, rotation)
        val byteBuffer = getImageByteBuffer(rotatedBitmap, inputSize)

        // ✅ Log model input tensor details
        interpreter?.getInputTensor(0)?.let {
            val shape = it.shape()  // e.g., [1, 224, 224, 3]
            val dataType = it.dataType()
            Log.d(TAG, "Model expects input shape: ${shape.joinToString()}, type: $dataType")
        }

        // ✅ Log input image shape
        Log.d(TAG, "Input bitmap size: ${rotatedBitmap.width}x${rotatedBitmap.height}")
        Log.d(TAG, "Bitmap pixel (0,0): ${rotatedBitmap[0, 0]}")

        val result = Array(1) { FloatArray(numClasses) }
        interpreter?.run(byteBuffer, result)
        return result[0]
    }


    fun getModelPrediction(
        bitmap: Bitmap, rotation: Int, topK: Int = 1, threshold: Float = 0.01f
    ): List<Classification> {
        val scores = classify(bitmap, rotation)

        // 🔍 Debugging: Log the raw model output
        Log.d(TAG, "Model output scores: ${scores.joinToString { "%.4f".format(it) }}")

        return scores.mapIndexed { index, score -> index to score }
            .filter { it.second >= threshold }.sortedByDescending { it.second }.take(topK)
            .map { (index, score) ->
                Classification(name = LoadModel.getLabel(index), score = score)
            }
    }


    //    function to close the delegate and interpreter
    fun close() {
        interpreter?.close()
        gpuDelegate?.close()
    }
}