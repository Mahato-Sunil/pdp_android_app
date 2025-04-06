package com.officialsunil.pdpapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaActionSound
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.CameraPreview
import com.officialsunil.pdpapplication.utils.CameraViewModel
import com.officialsunil.pdpapplication.utils.ImagePreview
import com.officialsunil.pdpapplication.utils.PermissionHandler
import com.officialsunil.pdpapplication.utils.saveImageToCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
                val homeIntent = Intent(this@CameraActivity, HomeActivity::class.java)
                startActivity(homeIntent)
                finish()
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
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val captureSound = MediaActionSound()
                    captureSound.play(MediaActionSound.SHUTTER_CLICK)

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

                    onPhotoCapture(rotatedBitmap)
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
        val controller = remember {
            LifecycleCameraController(applicationContext).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
                )
            }
        }

        //get reference to the camera view model
        val cameraViewModel = viewModel<CameraViewModel>()
        val bitmaps by cameraViewModel.bitmaps.collectAsState()

        val coroutineScope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }

        //main camera ui
        CameraActivityUI(
            showBottomSheet = { showBottomSheet = it },
            controller = controller,
            cameraViewModel = cameraViewModel,
            coroutineScope = coroutineScope
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                }, sheetState = sheetState
            ) {
                ImagePreview(
                    bitmaps = bitmaps,
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding(),
                    onDelete = {
                        coroutineScope.launch {
                            Toast.makeText(applicationContext, "Image Deleted", Toast.LENGTH_SHORT)
                                .show()
                            sheetState.hide()
                            showBottomSheet = false
                            cameraViewModel.clearBitmaps()
                        }
                    },
                    onSave = {
                        saveImageToCache(this@CameraActivity, bitmaps) },
                    coroutineScope = coroutineScope
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CameraActivityUI(
        showBottomSheet: (Boolean) -> Unit,
        controller: LifecycleCameraController,
        cameraViewModel: CameraViewModel,
        coroutineScope: CoroutineScope
    ) {
        // for the bottom sheet
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            CameraPreview(
                controller = controller, modifier = Modifier.fillMaxSize()
            )

            //top controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(10.dp)
                    .wrapContentHeight()
                    .background(colorResource(R.color.camera_transparent_background))
            ) {
//            camera switch
                IconButton(
                    onClick = {
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
                var flashModeState by remember { mutableStateOf("AUTO") }

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

                // close button
                val context = LocalContext.current
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val homeIntent = Intent(context, HomeActivity::class.java)
                            startActivity(homeIntent)
                            finish()
                        }
                    }) {
                    // camera swith button
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Camera",
                        tint = colorResource(R.color.font_color)
                    )
                }

            }

            // bottom Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(colorResource(R.color.camera_transparent_background))
                    .align(Alignment.BottomCenter)
            ) {
                IconButton(
                    onClick = {
                        showBottomSheet(true)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open Gallery Icon",
                        tint = colorResource(R.color.font_color),
                        modifier = Modifier.size(40.dp)
                    )
                }

                // capture button
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller, onPhotoCapture = cameraViewModel::onTakePhoto
                        )
                        showBottomSheet(true)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Capture Image",
                        tint = colorResource(R.color.font_color),
                        modifier = Modifier.size(40.dp)
                    )
                }

                //video button
                IconButton(
                    onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Capture Video",
                        tint = colorResource(R.color.font_color),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}