package com.officialsunil.pdpapplication.utils

import android.R.color.holo_green_dark
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.officialsunil.pdpapplication.model.Classification
import com.officialsunil.pdpapplication.viewui.PredictionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

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
    onSave: suspend () -> Unit,
    coroutineScope: CoroutineScope,
    classification: List<Classification>
) {
    if (bitmaps.isEmpty()) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Image Captured",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    } else {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            classification.forEach {
                Text(
                    text = "Prediction: ${it.name} \n Accuracy: ${it.score}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(holo_green_dark)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(50.dp))

                Image(
                    bitmap = bitmaps.last().asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier.padding(16.dp)
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch { onDelete() }
                    }) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Image"
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            onSave()
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Check, contentDescription = "Save Image"
                    )
                }
            }
        }
    }
}

// function to save the image to cache and show the preview
fun saveImageToCache(context: Context, bitmaps: List<Bitmap>) {
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

        Log.d("FILE", "Image Stored @ $absoluteFilePath")

    } catch (e: Exception) {
        e.printStackTrace()
        return
    }

    //start the intent to show the image predcition with preview
    val imagePredictionIntent = Intent(context, PredictionActivity::class.java)
    imagePredictionIntent.putExtra("image_path", absoluteFilePath)
    context.startActivity(imagePredictionIntent)
}

// function to close the bottomsheet
@OptIn(ExperimentalMaterial3Api::class)
suspend fun hideBottomSheet(
    sheetState: SheetState,
    showBottomSheet: (Boolean) -> Unit,
    cameraViewModel: CameraViewModel
) {
    showBottomSheet(false)
    sheetState.hide()
    cameraViewModel.clearBitmaps()
    Log.e("Capture Image", "Image Deleted sheetState : $sheetState")
}