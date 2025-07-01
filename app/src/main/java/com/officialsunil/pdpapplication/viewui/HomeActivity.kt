package com.officialsunil.pdpapplication.viewui

import android.app.Activity
import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.CustomDateTimeFormatter
import com.officialsunil.pdpapplication.utils.firebase.FirebaseUserCredentials
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.RetrievePredictionData
import com.officialsunil.pdpapplication.utils.firebase.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.firebase.ImageToBase64

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                InitHomeActivityUI()
            }
        }
    }
}

//  activity layout
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InitHomeActivityUI() {
    Scaffold(topBar = { HomeHeadingUI() }, bottomBar = {
        HomeButtonContainer()
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            HomeContainer()
        }
    }

}

/* ========================================================================
    UI Layout and Composable functions
   ========================================================================
 */
@Composable
fun HomeHeadingUI() {
    // get the current users name
    val currentUser = FirebaseUserCredentials.getCurrentUserCredentails()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.LightGray)

    ) {
        Image(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Icon",
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp, end = 16.dp)
        )

        Text(
            text = currentUser?.name ?: "Anonymous",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            style = TextStyle(
                letterSpacing = 2.sp,
            )
        )
    }
}

@Composable
fun HomeContainer() {
    val localContext = LocalContext.current

    // declare variables
    var predictionData by remember { mutableStateOf<RetrievePredictionData?>(null) }
    var isDataAvailable by remember { mutableStateOf(false) }

    // launche the effect to collect  the data
    LaunchedEffect(Unit) {
        val predictedData = FirebaseFirestoreUtils.fetchAllDiseaseInfo(
            userId = FirebaseUserCredentials.getCurrentUserCredentails()?.uid.toString(),
            onError = {
                // handle the error
                isDataAvailable = false
            })

        if (predictedData != null && predictedData.retrievePredictionData.isNotEmpty()) {
            predictionData = predictedData
            isDataAvailable = true
        } else {
            isDataAvailable = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // if there is no data show the prediction rationale
        if (!isDataAvailable) {
            NoDataRationale(onTakePictureClicked = {
                NavigationUtils.navigate(localContext, "camera")
            })
        } else {
            // show the  list of the data
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp)
                    .height(35.dp)
            ) {
                Text(
                    text = "Your diagnoses",
                    style = TextStyle(
                        color = colorResource(R.color.font_color),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                    ),
                )

                Text(
                    text = "View All", style = TextStyle(
                        color = Color.Blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.2.sp,
                        textDecoration = TextDecoration.Underline
                    ), modifier = Modifier.clickable {
                        NavigationUtils.navigate(localContext, "diagnosesList")
                    })
            }

            Spacer(modifier = Modifier.height(20.dp))

            // fill this area using the data `from the firebase
            val data = predictionData?.retrievePredictionData?.get(0)  // first data only
            Log.d("Firebase", "First Data : $data")
            val convertedBitmap = ImageToBase64.convertBase64ToImage(data?.imageBase64String ?: "")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .background(colorResource(R.color.light_card_background))
            ) {
                Image(
                    painter = if (convertedBitmap != null) BitmapPainter(convertedBitmap.asImageBitmap())
                    else painterResource(R.drawable.no_picture),
                    contentDescription = "Recent History Image",
                    modifier = Modifier.size(100.dp)
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //date
                    val formattedTime =
                        CustomDateTimeFormatter.formatDateTime(data?.timestamp.toString())
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )

                    Text(
                        text = data?.predictedName.toString(), style = TextStyle(
                            color = colorResource(R.color.font_color),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.1.sp,
                            textAlign = TextAlign.Start
                        ), modifier = Modifier
                            .fillMaxWidth(.75f)
                            .height(25.dp)
                    )
                }

                IconButton(
                    onClick = {
                        NavigationUtils.navigate(
                            context = localContext,
                            destination = "statistics",
                            data = data?.diseaseId.toString()
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "See More Icon",
                        tint = colorResource(R.color.font_color)
                    )
                }
            }
        }
    }
}

// prediction rationale to show if there is no any prediction data
@Composable
fun NoDataRationale(
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

// bottom section
@Composable
fun HomeButtonContainer() {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .height(120.dp)
            .background(Color.White)

    ) {
        //home
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(
                onClick = {
                    val currentActivity = context as? Activity
                    if (currentActivity !is HomeActivity) {
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        context.startActivity(intent)
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.House,
                    contentDescription = "Home Icon",
                    tint = Color(4, 32, 129, 255),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = "Home", style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.W600
                )
            )
        }

        // shutter button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(
                onClick = {
                    NavigationUtils.navigate(context, "camera", true)
                }) {
                Icon(
                    imageVector = Icons.Default.CameraEnhance,
                    contentDescription = "Camera Icon",
                    tint = Color(4, 32, 129, 255),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = "Capture", style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.W600
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(
                onClick = {
                    NavigationUtils.navigate(context, "accountCenter")
                }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Camera Icon",
                    tint = Color(4, 32, 129, 255),
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = "My Profile", style = TextStyle(
                        fontSize = 18.sp, fontWeight = FontWeight.W600
                    )
                )
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewHomeActivityUI() {
//    PDPApplicationTheme {
//        InitHomeActivityUI()
//    }
//}
