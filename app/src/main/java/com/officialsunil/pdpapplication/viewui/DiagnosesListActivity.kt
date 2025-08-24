/*
this class is responsible to show the prediction history by fetching the data from the firestore database

 */

package com.officialsunil.pdpapplication.viewui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.CustomDateTimeFormatter
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.RetrievePredictionData
import com.officialsunil.pdpapplication.utils.firebase.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.firebase.FirebaseUserCredentials
import com.officialsunil.pdpapplication.utils.firebase.ImageToBase64
import kotlinx.coroutines.launch

class DiagnosesListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                DiagnosesListScreen()
            }
        }
    }
}

@Composable
fun DiagnosesListScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = { DiagnosesHeadingUI(context) },
        modifier = Modifier
            .background(Color.White)
            .systemBarsPadding()

    ) { innerPadding ->
        DiagnosesContainer(
            modifier = Modifier.padding(innerPadding), context = context
        )
    }
}

@Composable
fun DiagnosesContainer(
    modifier: Modifier = Modifier, context: Context
) {
    // fetch the data from the firebase using coroutine scope
    val coroutineScope = rememberCoroutineScope()
    var predictionData by remember { mutableStateOf<RetrievePredictionData?>(null) }
    var isDataAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val data = FirebaseFirestoreUtils.fetchAllDiseaseInfo(
                userId = FirebaseUserCredentials.getCurrentUserCredentails()?.uid.toString(),
                onError = {
                    isDataAvailable = false
                })

            if (data != null && data.retrievePredictionData.isNotEmpty()) {
                predictionData = data
                isDataAvailable = true
            } else {
                isDataAvailable = false
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        // get the predictions from the firestore
        // if error show no prediction rationale else show the diagnoses list

        if (!isDataAvailable) {
            NoPredictionsRationale(onTakePictureClicked = {
                NavigationUtils.navigate(
                    context, "camera"
                )
            })
        } else {
            DiagnosesList(predictionData, onItemClicked = { dataToPass ->
                // pass the disease id
                NavigationUtils.navigate(
                    context = context, destination = "statistics", data = dataToPass
                )
            })

        }
    }
}

// prediction rationale to show if there is no any prediction data
@Composable
fun NoPredictionsRationale(
    onTakePictureClicked: () -> Unit
) {
    Spacer(Modifier.height(32.dp))
    Image(
        painter = painterResource(R.drawable.warning),
        contentDescription = "No Predictions",
        modifier = Modifier
            .size(120.dp)
            .clip(MaterialTheme.shapes.medium)
    )
    Spacer(Modifier.height(32.dp))
    Text(
        text = "Looks like you haven't scanned any images yet.",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
    )
    Spacer(Modifier.height(32.dp))
    Button(
        onClick = onTakePictureClicked,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        Text(text = "Take Picture")
    }
}

// diagnostic heading
@Composable
fun DiagnosesHeadingUI(context: Context) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(60.dp)
        ) {
            IconButton(
                onClick = {
                    NavigationUtils.navigate(context, "home", true)
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow Button ",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "Diagnoses List", style = TextStyle(
                    fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 1.2.sp
                ), textAlign = TextAlign.Start, modifier = Modifier.wrapContentSize()
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}

// composable function to show the diagnoses list
@Composable
fun DiagnosesList(
    predictionData: RetrievePredictionData?, onItemClicked: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth(.9f)
                .background(Color(240, 245, 255, 255))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DoubleArrow,
                contentDescription = "Info Icon",
                tint = Color.Blue
            )

            Text(
                text = "Click to View Complete Details", style = TextStyle(
                    fontSize = 12.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.sp
                ), color = Color.Blue, modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
    // columns to show the data
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()
    ) {

        items(predictionData?.retrievePredictionData?.size ?: 0) { index ->
            val prediction = predictionData?.retrievePredictionData?.get(index) ?: return@items
            // convert the base64 string back to image
            val convertedBitmap = ImageToBase64.convertBase64ToImage(prediction.imageBase64String)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clickable { onItemClicked(prediction.diseaseId) }
                    .background(Color.White)
                    .padding(horizontal = 16.dp)) {

                Image(
                    painter = if (convertedBitmap != null) {
                        BitmapPainter(convertedBitmap.asImageBitmap())
                    } else {
                        painterResource(R.drawable.no_picture)
                    },
                    contentDescription = prediction.predictedName,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = prediction.predictedName,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // get the formatted time from the timestamp
                    val formattedTime =
                        CustomDateTimeFormatter.formatDateTime(prediction.timestamp.toString())
//                    val formattedTime= prediction.timestamp
                    Log.d("Firebase Time", formattedTime)
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}