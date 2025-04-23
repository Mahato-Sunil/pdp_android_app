package com.officialsunil.pdpapplication.viewui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.DiagnosesList
import com.officialsunil.pdpapplication.utils.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.FirebaseUserCredentials
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme

class DiagnosesListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {}
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DiagnosesListUI(navigateTo: (String) -> Unit) {
    Scaffold(
        topBar = { DiagnosesHeadingUI(navigateTo) },
        modifier = Modifier
            .systemBarsPadding()
            .background(colorResource(R.color.light_background))
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            DiagnosesContainer(navigateTo)
        }
    }
}

@Composable
fun DiagnosesHeadingUI(navigateTo: (String) -> Unit) {
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
                    navigateTo("home")
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow Button ",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "My Predictions", style = TextStyle(
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

@Composable
fun DiagnosesContainer(navigateTo: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        DiagnosesListSkeleton(navigateTo)
    }
}

@Composable
fun DiagnosesListSkeleton(
    navigateTo: (String) -> Unit
) {
    // show the vertical scrollable bars for the scanned diseases
    val currentUser = FirebaseUserCredentials.getCurrentUserCredentails()
    val predictedInfo = lifecycleScope.launch {
        fetchDiagnosesList(currentUser.uid)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color.Yellow)
    ) {
        Image(
            imageVector = Icons.Default.Castle,
            contentDescription = "Predictions Image",
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(.2f)
                .fillMaxHeight(),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(18.dp))

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "20823-2342", style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    letterSpacing = 1.2.sp
                ), modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )

            Text(
                text = "Early Blight", style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    letterSpacing = 1.2.sp
                ), modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp)
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp), thickness = 1.dp, color = Color.LightGray
    )


}


// function to get the diagnoses list from the firestore
suspend fun fetchDiagnosesList(userId: String): List<DiagnosesList> {
    val retrievePredictionData = FirebaseFirestoreUtils.readPredictionData(userId) { err ->
        Log.e("Firestore", "Error : $err")
    }

    return retrievePredictionData?.let { listOf() } ?: emptyList()
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewDiagnosesUI() {
    DiagnosesListUI(
        navigateTo = { })
}