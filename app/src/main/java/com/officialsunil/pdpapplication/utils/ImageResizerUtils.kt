package com.officialsunil.pdpapplication.utils

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/*
this file mainly resizes the images to the size that model can take
 */

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) throw IllegalArgumentException(
        "Invalid Arguments for center cropping"
    )

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}

//scanner overlay
fun DrawScope.scannerOverlay(size: Size) {
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