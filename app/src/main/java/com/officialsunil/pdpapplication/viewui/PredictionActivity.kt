package com.officialsunil.pdpapplication.viewui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.firebase.Timestamp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.model.getPredictionDetails
import com.officialsunil.pdpapplication.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.DiseaseInformation
import com.officialsunil.pdpapplication.utils.FirebaseUserCredentials
import com.officialsunil.pdpapplication.utils.PredictionState
import com.officialsunil.pdpapplication.utils.SQLiteDatabaseEvent
import com.officialsunil.pdpapplication.utils.SQLiteDatabaseSchema
import com.officialsunil.pdpapplication.utils.SQLiteDatabaseViewModel
import com.officialsunil.pdpapplication.utils.convertImageToByteArray
import kotlinx.coroutines.launch

class PredictionActivity : ComponentActivity() {
    var isAlreadyUploaded = mutableStateOf(false)
    private lateinit var absolutePath: String
    private lateinit var diseaseName: String
    private lateinit var diseaseAccuracy: String
    private lateinit var imageBitmap: Bitmap

    // for working with database
    val db by lazy {
        Room.databaseBuilder(
            applicationContext, SQLiteDatabaseSchema::class.java, name = "predictions.db"
        ).build()
    }

    val viewModel by viewModels<SQLiteDatabaseViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SQLiteDatabaseViewModel(db.sqliteDatabaseInterface) as T
                }
            }
        })

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prediction = intent.getStringExtra("prediction")
        absolutePath = intent.getStringExtra("image_path").toString()
        imageBitmap = BitmapFactory.decodeFile(absolutePath)
        diseaseName = intent.getStringExtra("diseaseName").toString()
        diseaseAccuracy = intent.getStringExtra("diseaseAccuracy").toString()

        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                val state by viewModel.state.collectAsState()
                InitPredictionActivity(
                    state = state,
                    onEvent = viewModel::onEvent,
                    imageBitmap,
                    prediction.toString(),
                    saveImage = { state, onEvent ->
                        lifecycleScope.launch { savePredictionToDb(state, onEvent) }
                    },
                    isAlreadyUploaded = isAlreadyUploaded
                )

                // call the save prediction function automatically
                lifecycleScope.launch { savePredictionToDb(state, viewModel::onEvent) }
            }
        }
    }

    //    function to close the window
    fun closePreviewWindow() {
        val cameraIntent = Intent(this@PredictionActivity, CameraActivity::class.java)
        startActivity(cameraIntent)
        finish()
    }

    // function to save the image to sqlite database
    suspend fun savePredictionToDb(state: PredictionState, onEvent: (SQLiteDatabaseEvent) -> Unit) {
        // check if the data is already uploaded
        if (isAlreadyUploaded.value) {
            Toast.makeText(this, "Data already stored", Toast.LENGTH_SHORT).show()
            return
        }

        //get the current user's uid
        val currentUser = FirebaseUserCredentials.getCurrentUserCredentails()
        val userId = currentUser?.uid.toString()

        // convert image bitmap to the Bytearray
        val imageByteArray =
            convertImageToByteArray(this@PredictionActivity, absolutePath) ?: ByteArray(0)

        // set the values
        onEvent(SQLiteDatabaseEvent.SetUserId(userId))
        onEvent(SQLiteDatabaseEvent.SetPredictedName(diseaseName))
        onEvent(SQLiteDatabaseEvent.SetPredictedImage(imageByteArray))
        onEvent(SQLiteDatabaseEvent.SetAccuracy(diseaseAccuracy))
        onEvent(SQLiteDatabaseEvent.SetTimestamp(Timestamp.now().toString()))

        //  check the data is is available or not
        Log.d("Prediction", "User Id : ${state.userId}")
        Log.d("Prediction", "Name : ${state.name}")
        Log.d("Prediction", "Image : ${state.image}")
        Log.d("Prediction", "Accuracy : ${state.accuracy}")

        // save the prediction when all the data are availabel and set
        if (state.userId.isEmpty() || state.name.isEmpty() || state.image.isEmpty() || state.accuracy.isEmpty()) Toast.makeText(
            this, "Incomplete Data to store", Toast.LENGTH_SHORT
        ).show()
        else {
            onEvent(SQLiteDatabaseEvent.SavePrediction)
            isAlreadyUploaded.value = true
            Log.d("SQLite Database", "User Info saved")
        }
    }
}

@Composable
fun InitPredictionActivity(
    state: PredictionState,
    onEvent: (SQLiteDatabaseEvent) -> Unit,
    bitmap: Bitmap,
    prediction: String,
    saveImage: (state: PredictionState, onEvent: (SQLiteDatabaseEvent) -> Unit) -> Unit,
    isAlreadyUploaded: MutableState<Boolean>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(Color.White)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        PredictionHeader(
            state = state,
            onEvent = onEvent,
            saveImage = saveImage,
            isAlreadyUploaded = isAlreadyUploaded
        )
        Spacer(Modifier.height(20.dp))
        PredictionContainer(bitmap, prediction)
        PredictionDescriptionContainer(
            prediction = prediction, getPredictionDescription = ::getPredictionDetails
        )
    }
}

// function to show the prediction header
@Composable
fun PredictionHeader(
    state: PredictionState,
    onEvent: (SQLiteDatabaseEvent) -> Unit,
    saveImage: (state: PredictionState, onEvent: (SQLiteDatabaseEvent) -> Unit) -> Unit,
    isAlreadyUploaded: MutableState<Boolean>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 16.dp, end = 16.dp)
            .background(Color.Transparent)
    ) {
        val context = LocalContext.current
        IconButton(
            onClick = {
                if (context is PredictionActivity) context.closePreviewWindow()
            }) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Previous Icon",
                modifier = Modifier.size(28.dp),
                tint = colorResource(R.color.font_color)
            )
        }

        Text(
            text = "Diagnosis", style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            )
        )

        IconButton(
            onClick = {
                saveImage(state, onEvent)
            }) {

            val iconVector =
                if (isAlreadyUploaded.value) Icons.Default.CloudDone else Icons.Default.CloudOff
            Icon(
                imageVector = iconVector,
                contentDescription = "Cloud Icon",
                modifier = Modifier.size(28.dp),
                tint = Color.Gray
            )
        }
    }
    HorizontalDivider(
        color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth(.92f)
    )
}

//function to show the prediction body
@Composable
fun PredictionContainer(bitmap: Bitmap, prediction: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth(.9f)
                .background(colorResource(R.color.extra_light_card_background))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info Icon",
                tint = colorResource(R.color.font_color)
            )

            Text(
                text = "Please ensure the correctness of the Prediction", style = TextStyle(
                    fontSize = 12.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.sp
                ), color = Color.Gray, modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.height(10.dp))
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Captured Image",
            modifier = Modifier
                .fillMaxWidth(.8f)
                .height(200.dp)
                .padding(8.dp)
                .border(1.dp, colorResource(R.color.font_color), RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High
        )

        Text(
            text = prediction,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
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

    Spacer(Modifier.height(20.dp))
}

// function to show the other information based onthe predited disease
@Composable
fun PredictionDescriptionContainer(
    prediction: String, getPredictionDescription: (String) -> DiseaseInformation
) {
    val predictedResult = getPredictionDescription(prediction)

    // Dummy data for testing
//    val predictedResult = DiseaseInformation(
//        diseaseId = "potato_blight",
//        diseaseName = "Potato Blight",
//        diseaseDescription = "This is a fungal disease affecting the leaves of potato plants.",
//        diseaseCause = "Caused by poor drainage and extended exposure to moisture.",
//        diseaseSymptoms = "Yellowing leaves, dark spots on foliage, and curling leaf edges.",
//        diseaseTreatment = "Apply fungicides and ensure proper crop rotation."
//    )
    predictedResult.let { result ->
        PredictionDescriptions(
            title = "Description", description = result.diseaseDescription, isExpandedState = true
        )

        PredictionDescriptions(
            title = "Cause", description = result.diseaseCause
        )

        PredictionDescriptions(
            title = "Symptoms", description = result.diseaseSymptoms
        )

        PredictionDescriptions(
            title = "Treatment", description = result.diseaseTreatment
        )
    }
}

@Composable
fun PredictionDescriptions(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    isExpandedState: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(isExpandedState) }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent, contentColor = colorResource(R.color.font_color)
        ),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        modifier = modifier
            .fillMaxWidth(.95f)
            .padding(1.dp)
            .clickable { isExpanded = !isExpanded },

        ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, style = TextStyle(
                        letterSpacing = 1.2.sp
                    )
                )
                IconButton(
                    onClick = {
                        isExpanded = !isExpanded
                    }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Text(
                    text = description,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 32.dp, end = 16.dp, bottom = 12.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
