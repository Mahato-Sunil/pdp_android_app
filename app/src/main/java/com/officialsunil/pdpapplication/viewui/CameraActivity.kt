package com.officialsunil.pdpapplication.viewui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaActionSound
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.tfLiteModule.LiteRtClassifier
import com.officialsunil.pdpapplication.tfLiteModule.TFLiteModelAnalyzer
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.CameraViewModel
import com.officialsunil.pdpapplication.utils.Classification
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.PermissionHandler
import com.officialsunil.pdpapplication.utils.scannerOverlay
import kotlinx.coroutines.launch
import java.util.Locale


class CameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!PermissionHandler.checkCameraPermission(this)) PermissionHandler.requestCameraPermission(
            this, this
        )

        setContent {
            PDPApplicationTheme {
                InitCameraActivity(applicationContext)
            }
        }

        //on back pressed
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavigationUtils.navigate(this@CameraActivity, "home", true)
            }
        })
    }

    //  take photo function
    private fun takePhoto(
        controller: LifecycleCameraController, onPhotoCapture: (Bitmap) -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureStarted() {
                    super.onCaptureStarted()

                    val captureSound = MediaActionSound()
                    captureSound.play(MediaActionSound.SHUTTER_CLICK)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val originalBitmap = image.toBitmap()  // Only one conversion
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }

                    val rotatedBitmap = Bitmap.createBitmap(
                        originalBitmap,
                        0,
                        0,
                        originalBitmap.width,
                        originalBitmap.height,
                        matrix,
                        true
                    )
                    // crop the image
                    val boxSizeRatio = 0.8f
                    val boxSize =
                        (minOf(rotatedBitmap.width, rotatedBitmap.height) * boxSizeRatio).toInt()

                    val cropLeft = ((rotatedBitmap.width - boxSize) / 2f).toInt()
                    val cropTop = ((rotatedBitmap.height - boxSize) / 2f).toInt()
                    // Make sure the crop box stays within bounds
                    val safeCropLeft = cropLeft.coerceIn(0, rotatedBitmap.width - boxSize)
                    val safeCropTop = cropTop.coerceIn(0, rotatedBitmap.height - boxSize)

                    val croppedBitmap = Bitmap.createBitmap(
                        rotatedBitmap, safeCropLeft, safeCropTop, boxSize, boxSize
                    )

                    val resizedBitmap =
                        croppedBitmap.scale(224, 224)   // scale the bitmap to 224 by 224 size
                    onPhotoCapture(resizedBitmap)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("CAMERA", "Photo Captured Failed", exception)
                }
            })
    }

    /* ========================================================
    COMPOSABLE FUNCTION FOR UI LAYOUT
 */

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InitCameraActivity(applicationContext: Context) {
        var classification by remember {
            mutableStateOf(emptyList<Classification>())
        }

        val analyzer = remember {
            TFLiteModelAnalyzer(
                classifier = LiteRtClassifier(context = applicationContext), onResult = {
                    classification = it
                })
        }

        val controller = remember {
            LifecycleCameraController(applicationContext).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
                )

                //set the image analyzer for real time image analysis
                setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(applicationContext), analyzer
                )
            }
        }

        //get reference to the camera view model
        val cameraViewModel = viewModel<CameraViewModel>()
        val bitmaps by cameraViewModel.bitmaps.collectAsState()

        val coroutineScope = rememberCoroutineScope()
        var showPrediction by remember { mutableStateOf(false) }

        //main camera ui
        CameraActivityUI(
            showPrediction = { showPrediction = it },
            controller = controller,
            cameraViewModel = cameraViewModel,
            classifications = classification
        )

        var freezePrediction by remember { mutableStateOf<Classification?>(null) }

        if (showPrediction && freezePrediction == null) freezePrediction =
            classification.maxByOrNull { it.score }

        freezePrediction?.let { prediction ->
            ImagePreview(
                bitmaps = bitmaps,
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(),
                onDelete = {
                    coroutineScope.launch {
                        freezePrediction = null
                        showPrediction = false
                        cameraViewModel.clearBitmaps()

                    }

                },
                onSave = { prediction ->
                    freezePrediction = null
                    showPrediction = false
                    saveImageToCache(this@CameraActivity, bitmaps, prediction)

                },

                coroutineScope = coroutineScope,
                classification = prediction
            )
        }
    }
//    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CameraActivityUI(
        showPrediction: (Boolean) -> Unit,
        controller: LifecycleCameraController,
        cameraViewModel: CameraViewModel,
        classifications: List<Classification>
    ) {
        val context = LocalContext.current  // for camera controller
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            CameraPreview(
                controller = controller, modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        compositingStrategy = CompositingStrategy.Offscreen
                    )
                    .drawWithContent {
                        drawContent()
                        scannerOverlay(size)
                    })

            //top controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(colorResource(R.color.camera_transparent_background))
                    .align(Alignment.TopCenter)
            ) {
//            camera switch
                IconButton(
                    onClick = {
                        // get the camera provider
                        controller.cameraSelector =
                            if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                            else CameraSelector.DEFAULT_BACK_CAMERA
                    }) {

                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = colorResource(R.color.font_color)
                    )
                }

                //flash light icon
                var flashModeState by remember { mutableStateOf("OFF") }

                IconButton(
                    onClick = {
                        when (controller.imageCaptureFlashMode) {
                            FLASH_MODE_ON -> {
                                controller.imageCaptureFlashMode = FLASH_MODE_OFF
                                flashModeState = "OFF"
                            }

                            FLASH_MODE_OFF -> {
                                controller.imageCaptureFlashMode = FLASH_MODE_AUTO
                                flashModeState = "AUTO"
                            }

                            else -> {
                                controller.imageCaptureFlashMode = FLASH_MODE_ON
                                flashModeState = "ON"
                            }
                        }
                    }) {
                    val iconState = when (flashModeState) {
                        "ON" -> Icons.Default.FlashOn
                        "OFF" -> Icons.Default.FlashOff
                        else -> Icons.Default.FlashAuto
                    }
                    Icon(
                        imageVector = iconState,
                        contentDescription = "Flash Icon",
                        tint = colorResource(R.color.font_color)
                    )
                }

                IconButton(
                    onClick = {
                        NavigationUtils.navigate(context, "home", true)
                    }) {
                    // camera switch button
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Camera",
                        tint = colorResource(R.color.font_color)
                    )
                }

            }

            Row(Modifier.zIndex(1f)) {
                classifications.forEach {

                    val textColorMode: Color = when {
                        it.score >= 0.8 -> Color.Green
                        it.score >= 0.7 -> colorResource(R.color.font_color)
                        else -> Color.Red
                    }

                    Text(
                        text = "Prediction : ${it.name} \n Accuracy : ${
                            String.format(
                                Locale.US, "%.2f", it.score * 100
                            )
                        }",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        style = TextStyle(
                            color = textColorMode, letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                        ),
                        modifier = Modifier
                            .offset(y = 120.dp)
                            .fillMaxWidth()
                            .background(colorResource(R.color.camera_transparent_background))
                            .wrapContentHeight()
                            .zIndex(1f),
                        fontWeight = FontWeight(400)
                    )
                }
            }

            // bottom Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(colorResource(R.color.camera_transparent_background))
                    .align(Alignment.BottomCenter)
            ) {
                // capture button
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller, onPhotoCapture = cameraViewModel::onTakePhoto
                        )
                        showPrediction(true)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Capture Image",
                        tint = colorResource(R.color.font_color),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}