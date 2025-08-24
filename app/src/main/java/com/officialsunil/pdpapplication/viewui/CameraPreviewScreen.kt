/*
This class is reponsible to show the camera feed in the activity
also  handles saving the image data the cache
 */
package com.officialsunil.pdpapplication.viewui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.tfLiteModule.ImagePreprocessing.uriToBitmap
import com.officialsunil.pdpapplication.tfLiteModule.LiteRtClassifier
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.CameraViewModel
import com.officialsunil.pdpapplication.utils.Classification
import com.officialsunil.pdpapplication.utils.NavigationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

internal const val TAG = "CameraPreview"

class CameraPreviewScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the data from the previous activity
        val imageUri = intent.getStringExtra("imageUri")
        val predictedName = intent.getStringExtra("predictedName")
        val predictedScore = intent.getFloatExtra("predictedScore", 0f)

        // get the bitmap from the uri
        val uri = imageUri?.toUri()
        val bitmap = uri?.let { uriToBitmap(this@CameraPreviewScreen.contentResolver, it) }

        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                val cameraViewModel = viewModel<CameraViewModel>()
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .background(Color.White)
                        .systemBarsPadding()

                ) {
//                    Text("Prediction Result", style = TextStyle(fontSize = 24.sp))
//                    Text("Name : $predictedName \nAccuracy : $predictedScore \nURI : $imageUri")
                    bitmap?.let {
                        ImagePreview(
                            bitmaps = it,
                            classification = Classification(
                                name = predictedName.toString(), score = predictedScore
                            ),
                            onDelete = {
                                cameraViewModel.clearBitmap()
                                cameraViewModel.clearPredictions()
                                NavigationUtils.navigate(
                                    this@CameraPreviewScreen,
                                    "home",
                                    finish = true
                                )
                            },
                            onSave = { classification ->
                                saveImageToCache(
                                    context = this@CameraPreviewScreen, it, classification
                                )
                            },
                            coroutineScope = rememberCoroutineScope(),
                        )
                    }

                }
            }
        }
    }
}

// function for showing the live camera feed
@Composable
fun CameraPreview(
    controller: LifecycleCameraController, modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                scaleType = PreviewView.ScaleType.FIT_CENTER
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ImagePreview(
    bitmaps: Bitmap?,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onSave: suspend (Classification) -> Unit,
    coroutineScope: CoroutineScope,
    classification: Classification
) {
    if (bitmaps == null) {
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
                bitmap = bitmaps.asImageBitmap(),
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
fun saveImageToCache(context: Context, bitmaps: Bitmap, prediction: Classification) {
    var absoluteFilePath: String
    bitmaps.let {
        val suffix =
            SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(System.currentTimeMillis())
        val nameFormat = "pdp_img_$suffix.jpg"
        val tempFile = File(context.cacheDir, nameFormat)

        try {
            FileOutputStream(tempFile).use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, 100, out)
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
}
