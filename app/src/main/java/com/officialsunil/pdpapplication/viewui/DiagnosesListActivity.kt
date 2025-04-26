package com.officialsunil.pdpapplication.viewui

import android.annotation.SuppressLint
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.DiagnosesList
import com.officialsunil.pdpapplication.utils.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.FirebaseUserCredentials
import com.officialsunil.pdpapplication.utils.PredictionState
import com.officialsunil.pdpapplication.utils.Predictions
import com.officialsunil.pdpapplication.utils.SQLiteDatabaseEvent
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class DiagnosesListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        val currentUser = FirebaseUserCredentials.getCurrentUserCredentails()

        setContent {
            // define the prediction data list to store
//            val predictionList = mutableListOf<DiagnosesList>()
//            currentUser?.uid?.let {
//                val coroutineScope = rememberCoroutineScope()
//                LaunchedEffect(Unit) {
//                    coroutineScope.launch {
//                        val fetchedPredictionData = getDiagnosesList(currentUser.uid)
//                        predictionList.clear()
//                        predictionList.addAll(fetchedPredictionData)
//                    }
//                }
//            }

            // initialize the theme
            PDPApplicationTheme {
                DiagnosesListUI(
                    navigateTo = { destination -> navigateTo(destination) },
//                    predictionList = predictionList
                )
            }
        }
    }

    // function to navigate based on the query sting
    fun navigateTo(destination: String) {
        val intent = when (destination) {
            "home" -> Intent(this, HomeActivity::class.java)
            "statistics" -> Intent(this, StatisticsActivity::class.java)
            "camera" -> Intent(this, CameraActivity::class.java)
            else -> Intent(this, DiagnosesListActivity::class.java)
        }
        startActivity(intent)
        if (destination == "home") finish()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DiagnosesListUI(
    navigateTo: (String) -> Unit,
//                    predictionList: List<DiagnosesList>
) {
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
            DiagnosesContainer(
                navigateTo,
//                predictionList
            )
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
fun DiagnosesContainer(
    navigateTo: (String) -> Unit,
//                       predictionList: List<DiagnosesList>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        // show the image not found rationale
//        if (predictionList.isEmpty()) {
        if (1 == 2)
        //there is no data
        // ask user to navigate to the camera activity for taking the pictures

            Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.warning),
            contentDescription = "No Prediction Data",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(120.dp)
                .clip(shape = CircleShape)
        )

        Spacer(Modifier.height(50.dp))
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Looks like you've not scanned any images, Capture leaf picture now to make predictions ",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        letterSpacing = 1.2.sp,
                        color = Color.Gray
                    )
                )

                OutlinedButton(
                    shape = RoundedCornerShape(16.dp), colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = colorResource(R.color.font_color),
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = Color.LightGray
                    ), onClick = {
                        navigateTo("camera")
                    }, modifier = Modifier
                        .fillMaxWidth(.8f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Take Picture"
                    )
                }
            }

        }
    } else DiagnosesListSkeleton(navigateTo,
//    predictionList
    )
}
}

@Composable
fun DiagnosesListSkeleton(
    navigateTo: (String) -> Unit,
    state: PredictionState,
    onEvent: (SQLiteDatabaseEvent) -> Unit
//    predictionList: List<DiagnosesList>
) {
//    predictionList.forEach { prediction ->
    // before hand => convert the list of int to byte array and then to image bitmap
    // to show the images

    // use lazy column for showing the list view
    LazyColumn(
        contentPadding = PaddingValues(1.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        items (state.predictions) { predictions ->

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White)
                    .clickable {
                        navigateTo("statistics")
                    }) {
                Image(
                    bitmap =,
                    contentDescription = prediction.name,
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
                        text = prediction.timestamp.toString(), style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            letterSpacing = 1.2.sp
                        ), modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )

                    Text(
                        text = prediction.name, style = TextStyle(
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
                    .padding(horizontal = 10.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }

    }
}

//function to convert the list of  int to bitmap for the  image displaying
fun convertToImageBitmap(imageIntList: List<Int>): Bitmap {
//    val byteBuffer =
//        ByteBuffer.allocate(imageIntList.size * Int.SIZE_BYTES)    // declare the size for each int
//    imageIntList.forEach { byteBuffer.putInt(it) }

//    val imageByteArray = byteBuffer.array() // generates the bytearray from the int array

    val imageByteArray = imageIntList.map { it.toByte() }.toByteArray()
    /*
    Convert the imageByteArray to imageBitmap
    Steps :-
    => take the raw image data from imageByteArray
    => user Android BitmapFactory class to decode it into a bitmap object
    => Provide additional options for  enhancing the quality of the iamge that is being generated
    => return the bitmap object
     */

    val bitmapOptions = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888         // default highest quality
    }

    val imageBitmap =
        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size, bitmapOptions)
    if (imageBitmap == null) Log.e("Prediction Image", "Failed to Decode the Array to Image Bitmap")
    Log.d("Prediction Image", "Image config : ${imageBitmap.config}")

    // return the image bitmap
    return imageBitmap
}

// function to get the diagnoses list from the firestore
suspend fun getDiagnosesList(userId: String): List<DiagnosesList> {
    val retrievePredictionData = FirebaseFirestoreUtils.readPredictionData(userId) { err ->
        Log.e("Firestore", "Error : $err")
    }
    Log.d("Firestore", "Prediction Data : $retrievePredictionData")

    return retrievePredictionData?.retrievePredictionData?.map { data ->
        val imageBitmap = convertToImageBitmap(data.imageListArray)
        // extract the required values
        DiagnosesList(
            image = imageBitmap, name = data.predictedName, timestamp = data.timestamp
        )
    } ?: emptyList()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewDiagnosesUI() {
    DiagnosesListUI(
        navigateTo = { }, predictionList = listOf()
    )
}