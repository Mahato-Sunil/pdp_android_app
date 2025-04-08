package com.officialsunil.pdpapplication.viewui

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme

class PredictionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the file path
        // testing
        val absolutePath = intent.getStringExtra("image_path")
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                InitPredictionActivity(absolutePath.toString())
            }
        }
    }

}

@Composable
fun InitPredictionActivity(absolutePath: String) {

    val bitmap = BitmapFactory.decodeFile(absolutePath)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorResource(R.color.light_background))
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Captured Image",
        )
    }

}

//ImagePreview(
//                    bitmaps = bitmaps,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(colorResource(R.color.extra_light_card_background))
//                        .systemBarsPadding(),
//                    onDelete = {
//                        coroutineScope.launch {
//                            hideBottomSheet(
//                                sheetState,
//                                showBottomSheet = { showBottomSheet = it },
//                                cameraViewModel
//                            )
//                        }
//
//                    },
//                    onSave = {
//                        saveImageToCache(this@CameraActivity, bitmaps)
//                    },
//                    coroutineScope = coroutineScope,
//                    classification = classification
//                )
