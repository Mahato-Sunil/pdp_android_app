package com.officialsunil.pdpapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import kotlinx.coroutines.withContext

/*
this file mainly resizes the images to the size that model can take

 */

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) throw IllegalArgumentException(
        "Invalid Arguments for center cropping"
    )

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredWidth)
}

//scanner overlay
fun DrawScope.scannerOverlay(size: Size) {
//    val width = size.width
//    val height = size.height
//    val boxSize = 256.dp.toPx()
//    val left = (width - boxSize) / 2
//    val top = (height - boxSize) / 2

    val boxSizeRatio = 0.8f // 80% of the width
    val boxSize = size.width * boxSizeRatio
    val left = (size.width - boxSize) / 2f
    val top = (size.height - boxSize) / 2f

    drawRect(color = Color(0x88000000))  // Semi-transparent overlay

    drawRect(
        color = Color.Transparent,
        topLeft = Offset(left, top),
        size = Size(boxSize, boxSize),
        blendMode = BlendMode.Clear  // Create transparent area
    )

    drawRect(
        color = Color.Blue,
        topLeft = Offset(left, top),
        size = Size(boxSize, boxSize),
        style = Stroke(
            width = 4.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )  // White border
    )
}

/*
Convert the image to byte array for storage in firebase
use the Coil for better compression function
perfect for modern kotlin based android apps
- avoids bitmap memory bloat
runs on background thread using Dispatchers.IO
flexible to support files paths uris or remote images

 */

/*

Purpose: Converts an image from a file path or URI into a compressed JPEG byte array, ensuring it stays under maxSize bytes (default: 1MB).

Suspending function: Runs asynchronously with withContext(Dispatchers.IO) to keep it off the main/UI thread.

 */
suspend fun convertImagetoByteArray(
    context: Context, imageUri: String, // file path
    maxSize: Int = 1024 * 500     // 500 kb

): ByteArray? =
    withContext(Dispatchers.IO) { //This is used because loading and compressing bitmaps are expensive operations and should not block the UI thread.

        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(imageUri)
            .size(400) // Resize image to a max width/height of 800px
            .allowHardware(false) // is important because hardware bitmaps cannot be converted into byte arrays or manipulated directly.
            .build()

        val result = imageLoader.execute(request)

        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap ?: return@withContext null

        var quality = 95
        var byteArray: ByteArray

        do {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.JPEG, quality, outputStream)
            byteArray = outputStream.toByteArray()
            quality -= 5
        } while (byteArray.size > maxSize && quality > 50)

        byteArray
    }