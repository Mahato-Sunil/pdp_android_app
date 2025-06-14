package com.officialsunil.pdpapplication.tfLiteModule

import android.content.Context
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


/*
This object is responsible to load  the  model  from the model path
model path : provided by users in run time from LiteRtClassifier class

also use this object class to load the label from the label.txt file

 */

object LoadModel {
    lateinit var labels: List<String>

    fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        var declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun loadLabel(context: Context, labelPath: String) {
        labels = context.assets.open(labelPath).bufferedReader().readLines()
    }

    fun getLabel(index: Int): String = labels.getOrElse(index) { "Unknown" }
}
