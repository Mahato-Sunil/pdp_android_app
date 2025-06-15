package com.officialsunil.pdpapplication.viewui

import android.os.Bundle
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.DiseaseInfo.getPredictionDetails
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.PredictionData
import com.officialsunil.pdpapplication.utils.firebase.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.firebase.FirebaseUserCredentials
import com.officialsunil.pdpapplication.utils.firebase.ImageToBase64
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme
import kotlinx.coroutines.launch

class StatisticsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // get the disease id from the previous intent and also pass the user id
        val diseaseIdRef = intent.getStringExtra("diseaseId")
        setContent {
            PDPApplicationTheme {
                StatisticsUILayout(diseaseIdRef = diseaseIdRef.toString())
            }
        }
    }
}

//ui for the statistics activity
@Composable
fun StatisticsUILayout(diseaseIdRef: String) {
    Scaffold(
        topBar = { StatsHeading(diseaseIdRef) },
        modifier = Modifier.systemBarsPadding()
    ) { innerPadding ->
        StatsContainer(
            diseaseIdRef = diseaseIdRef,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// heading section
@Composable
fun StatsHeading(diseaseIdRef: String) {
    val context = LocalContext.current
    val diseaseData = fetchDiseaseInformation(diseaseIdRef)
    val headingTitle = diseaseData?.predictedName ?: "Statistics"

    Column(
        modifier = Modifier.background(Color.White)
    ) {
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
                    NavigationUtils.navigate(context, "diagnosesList")
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow Button ",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = headingTitle, style = TextStyle(
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

// main container
@Composable
fun StatsContainer(diseaseIdRef: String, modifier: Modifier) {
    val predictions = fetchDiseaseInformation(diseaseIdRef)

    // convert the base 64 image back to the bitmap image
    val imageBitmap = ImageToBase64.convertBase64ToImage(predictions?.imageBase64String.toString())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Predicted Image",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.no_picture),
                    contentDescription = "Fallback Image",
                    modifier = Modifier.size(200.dp)
                )
            }
//            Image(
//                bitmap = imageBitmap?.asImageBitmap() ?: ,
//                contentDescription = "Disease Image",
//                contentScale = ContentScale.Fit,
//                modifier = Modifier.fillMaxSize()
//
//            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val predictionAccuracy = predictions?.accuracy
        Text(
            text = "Predicted Accuracy: $predictionAccuracy",
            style = TextStyle(fontSize = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Damage Scale", textAlign = TextAlign.Start, style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.Black
            ), modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // show the  severity level in the circular progress indicatior
        /*
            logic to show the severity level
            0 - 45% ==> Green
            46 - 64% ==> Orange
            65 - 100% ==> Red

            get the  severity level from the utility function
         */
//        val severityLevel = predictionAccuracy?.toFloat() ?: 0f

        val severityLevel = .56f

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            CircularProgressIndicator(
                progress = { severityLevel },
                modifier = Modifier.fillMaxSize(),
                color = if (severityLevel in 0.0..0.45) Color.Green
                else if (severityLevel in 0.46..0.64) Color(255, 160, 0, 255)
                else Color.Red,

                strokeWidth = 14.dp,
                trackColor = Color.LightGray,
                strokeCap = StrokeCap.Butt,
            )

            Text(
                text = "$severityLevel %",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = if (severityLevel in 0.0..0.45) Color.Green
                else if (severityLevel in 0.46..0.64) Color(255, 160, 0, 255)
                else Color.Red,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // disease description
        val diseaseDesc = getPredictionDetails(predictions?.predictedName ?: "unknown")

        // disease description
        SectionCard(
            title = "About", content = diseaseDesc.diseaseDescription
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 4️⃣ Causes
        SectionCard(
            title = "Causes", content = diseaseDesc.diseaseCause
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 5️⃣ Effects
        SectionCard(
            title = "Effects", content = diseaseDesc.diseaseSymptoms
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 6️⃣ Prevention
        SectionCard(
            title = "Prevention", content = diseaseDesc.diseaseTreatment
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Reusable card for each section
@Composable
fun SectionCard(title: String, content: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(250, 250, 250, 255)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = content, style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
            )
        }
    }
}

// function to fetch the particular disease information
@Composable
fun fetchDiseaseInformation(diseaseId: String): PredictionData? {
    // fetch the data from the firebase using coroutine scope
    val coroutineScope = rememberCoroutineScope()
    var diseaseData by remember { mutableStateOf<PredictionData?>(null) }
    var isDataAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val data = FirebaseFirestoreUtils.fetchDiseaseInfo(
                userId = FirebaseUserCredentials.getCurrentUserCredentails()?.uid.toString(),
                diseaseId = diseaseId,
                onError = {
                    isDataAvailable = false
                })

            if (data != null) {
                diseaseData = data
                isDataAvailable = true
            } else {
                isDataAvailable = false
            }
        }
    }
    return diseaseData
}