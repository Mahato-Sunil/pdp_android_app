package com.officialsunil.pdpapplication.utils

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/*
this file mainly resizes the images to the size that model can take

 */

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap{
    val xStart = (width -desiredWidth) /2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart <0 || desiredWidth > width || desiredHeight > height)
    throw IllegalArgumentException("Invalid Arguments for center cropping")

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredWidth)
}

//scanner overlay
fun DrawScope.scannerOverlay(size: Size) {
    val width = size.width
    val height = size.height
    val boxSize = 256.dp.toPx()
    val left = (width - boxSize) / 2
    val top = (height - boxSize) / 2

    drawRect(color = Color(0x88000000))  // Semi-transparent overlay

    drawRect(
        color = Color.Transparent,
        topLeft = Offset(left, top),
        size = Size(boxSize, boxSize),
        blendMode = BlendMode.Clear  // Create transparent area
    )

    drawRect(
        color = Color.White,
        topLeft = Offset(left, top),
        size = Size(boxSize, boxSize),
        style = Stroke(width = 4.dp.toPx())  // White border
    )
}