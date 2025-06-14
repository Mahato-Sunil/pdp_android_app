package com.officialsunil.pdpapplication.tfLiteModule

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.core.graphics.scale
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ImagePreprocessing {
    //  pre-process the images bitmap
    // rotate the bitmap
    fun getRotatedBitmap(bitmap: Bitmap, rotation: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    //scale and return the byte buffer

    fun getImageByteBuffer(bitmap: Bitmap, inputSize: Int): ByteBuffer {
        val scaledBitmap = bitmap.scale(inputSize, inputSize)
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        scaledBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        return byteBuffer
    }
}