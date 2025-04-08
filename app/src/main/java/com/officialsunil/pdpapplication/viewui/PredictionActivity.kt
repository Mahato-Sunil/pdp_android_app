package com.officialsunil.pdpapplication.viewui

import android.R.color.holo_green_dark
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.model.Classification
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.CameraViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PredictionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val absolutePath = intent.getStringExtra("image_path")
        val prediction = intent.getStringExtra("prediction")
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                InitPredictionActivity(absolutePath.toString(), prediction.toString())
            }
        }
    }

}

@Composable
fun InitPredictionActivity(absolutePath: String, prediction: String) {
    val bitmap = BitmapFactory.decodeFile(absolutePath)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorResource(R.color.light_background))
            .systemBarsPadding()
    ) {
        Spacer(Modifier.height(50.dp))

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Captured Image",
            modifier = Modifier
                .size(360.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.High
        )

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Name: ${prediction}",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight(400),
                letterSpacing = 1.5.sp,
                lineHeight = 36.sp,
                color = colorResource(R.color.font_color)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            textAlign = TextAlign.Center
        )
    }

}
