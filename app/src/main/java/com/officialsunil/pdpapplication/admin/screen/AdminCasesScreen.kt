package com.officialsunil.pdpapplication.admin.screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.admin.screen.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.CustomDateTimeFormatter
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.RetrieveDiseaseInformation
import kotlinx.coroutines.launch

class AdminCasesScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                AdminCasesScreenUI()
            }
        }
    }
}

@Composable
fun AdminCasesScreenUI() {
    Scaffold(
        topBar = { AdminHeaderUI() },
        modifier = Modifier
            .systemBarsPadding()
            .background(colorResource(R.color.light_background))
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            AdminContainerUI(
                modifier = Modifier.padding(innerPadding), context = LocalContext.current
            )
        }
    }
}

// header
@Composable
fun AdminHeaderUI() {
    val context = LocalContext.current
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(60.dp)
        ) {
            IconButton(
                onClick = {
                    NavigationUtils.navigate(context, "adminHome", true)
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Arrow Back",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "Cases", style = TextStyle(
                    fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 1.2.sp
                ), textAlign = TextAlign.Start, modifier = Modifier.wrapContentSize()
            )

            Spacer(Modifier.weight(1f))
            // add button
            IconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(2.dp)
                    .weight(1f)
                    .clip(CircleShape)
                    .background(Color.Green),
                onClick = {
                    NavigationUtils.navigate(context, "addDisease")
                }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Disease",
                    modifier = Modifier.size(30.dp)
                )
            }

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

// main Container 
@Composable
fun AdminContainerUI(modifier: Modifier = Modifier, context: Context) {
    // fetch the data from the firebase using coroutine scope
    val coroutineScope = rememberCoroutineScope()
    var diseaseData by remember { mutableStateOf<RetrieveDiseaseInformation?>(null) }
    var isDataAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
//            val data = FirebaseFirestoreUtils.fetchAllDiseaseInfo(
//                userId = FirebaseUserCredentials.getCurrentUserCredentails()?.uid.toString(),
//                onError = {
//                    isDataAvailable = false
//                })
//
//            if (data != null && data.retrievePredictionData.isNotEmpty()) {
//                diseaseData = data
//                isDataAvailable = true
//            } else {
//                isDataAvailable = false
//            }
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
            Text("No Diseases !")
        } else {
            DiseaseList(diseaseData, onItemClicked = { dataToPass ->
                // pass the disease id
                NavigationUtils.navigate(
                    context = context, destination = "diseaseInfo", data = dataToPass
                )
            })

        }
    }
}

@Composable
fun DiseaseList(
    diseaseData: RetrieveDiseaseInformation?, onItemClicked: (String) -> Unit
) {
    // columns to show the data
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()
    ) {

        items(diseaseData?.retrieveDiseaseInformation?.size ?: 0) { index ->
            val data = diseaseData?.retrieveDiseaseInformation?.get(index) ?: return@items
            Log.d("Admin", "Cases Screen  : ${data}")
            // convert the base64 string back to image
//            val convertedBitmap = ImageToBase64.convertBase64ToImage(prediction.imageBase64String)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
//                    .clickable { onItemClicked() }
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {

                Image(
                    painterResource(R.drawable.no_picture),
//                    contentDescription = data.,Test
                    contentDescription = "",
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
                        text = "Sample Disease",
//                        text = data.predictedName,
                        style = MaterialTheme.typography.titleMedium
                    )

//                    // get the formatted time from the timestamp
//                    val formattedTime =
//                        CustomDateTimeFormatter.formatDateTime(data.timestamp.toString())
////                    val formattedTime= prediction.timestamp
//                    Log.d("Firebase Time", formattedTime)
//                    Text(
//                        text = formattedTime,
//                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
//                    )
                }
            }
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}