/*
This class is reponsible to show the camera feed in the activity
also  handles saving the image data the cache
 */
package com.officialsunil.pdpapplication.viewui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.Classification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

internal const val TAG = "Camera Preview"

// function for showing the live camera feed
@Composable
fun CameraPreview(
    controller: LifecycleCameraController, modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ImagePreview(
    bitmaps: List<Bitmap>,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onSave: suspend (Classification) -> Unit,
    coroutineScope: CoroutineScope,
    classification: Classification
) {
    if (bitmaps.isEmpty()) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .background(colorResource(R.color.light_background))
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Image Captured", style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.light_background))
        ) {
            Text(
                text = "Prediction: ${classification.name} \n Accuracy: ${
                    String.format(
                        Locale.US, "%.2f", classification.score * 100
                    )
                } %",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    letterSpacing = 1.4.sp,
                    color = colorResource(R.color.font_color)
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )

            Image(
                bitmap = bitmaps.last().asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = modifier
                    .size(400.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20).dp)
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch { onDelete() }
                    }) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Image",
                        modifier = modifier.fillMaxSize(),
                        tint = colorResource(R.color.font_color)
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onSave(classification)
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save Image",
                        modifier = modifier.fillMaxSize(),
                        tint = colorResource(R.color.font_color)
                    )
                }
            }

        }
    }
}


// function to save the image to cache and show the preview
fun saveImageToCache(context: Context, bitmaps: List<Bitmap>, prediction: Classification) {
    var absoluteFilePath: String

    if (bitmaps.isEmpty()) return

    val suffix = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(System.currentTimeMillis())
    val nameFormat = "pdp_img_$suffix.jpg"
    val tempFile = File(context.cacheDir, nameFormat)

    try {
        FileOutputStream(tempFile).use { out ->
            bitmaps.last().compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
        }
        absoluteFilePath = tempFile.absolutePath // Useful to pass to your ML model
        Log.d(TAG, "Image Stored @ $absoluteFilePath")
    } catch (e: Exception) {
        e.printStackTrace()
        return
    }

    // for debugginh
    Log.d(
        TAG,
        "Data Received as \n     Name : ${prediction.name} \n Raw Accuracy : ${prediction.score}"
    )

    //start the intent to show the image predcition with preview
    val predictionFormat =
        prediction.name + " (" + String.format(Locale.US, "%.2f", prediction.score * 100) + "%)"
    val imagePredictionIntent = Intent(context, PredictionActivity::class.java)
    imagePredictionIntent.putExtra("image_path", absoluteFilePath)
    imagePredictionIntent.putExtra("prediction", predictionFormat)
    imagePredictionIntent.putExtra("diseaseName", prediction.name)
    imagePredictionIntent.putExtra(
        "diseaseAccuracy", String.format(Locale.US, "%.2f", prediction.score * 100)
    )
    Log.d(TAG, "Image Saved To cache")
    context.startActivity(imagePredictionIntent)
}
