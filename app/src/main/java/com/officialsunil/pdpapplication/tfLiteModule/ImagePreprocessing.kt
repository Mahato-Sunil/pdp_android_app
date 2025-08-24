package com.officialsunil.pdpapplication.tfLiteModule

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.core.graphics.scale
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ImagePreprocessing {
    //  pre-process the images bitmap
//    convert the  image to bitmap
    fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        try {
            val source = ImageDecoder.createSource(contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
            }
            return bitmap
        } catch (exception: Exception) {
            Log.e("ImagePreprocessing", "Error converting image to bitmap:\n $exception")
            return null
        }
    }

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