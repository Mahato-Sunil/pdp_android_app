package com.officialsunil.pdpapplication.viewui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
import com.google.firebase.Timestamp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.DiseaseInfo.getPredictionDetails
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.PredictionData
import com.officialsunil.pdpapplication.utils.firebase.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.firebase.FirebaseUserCredentials
import com.officialsunil.pdpapplication.utils.firebase.ImageToBase64
import java.util.Base64
import java.util.Random

class PredictionActivity : ComponentActivity() {
    var isAlreadyUploaded = mutableStateOf(false)

    private lateinit var completeLabel: String
    private lateinit var predictedImgPath: String
    private lateinit var predictedDiseaseName: String
    private lateinit var predictedDiseaseAccuracy: String
    private lateinit var predictedImageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the data from previous activity
        completeLabel = intent.getStringExtra("prediction").toString()
        predictedImgPath = intent.getStringExtra("image_path").toString()
        predictedImageBitmap = BitmapFactory.decodeFile(predictedImgPath)
        predictedDiseaseName = intent.getStringExtra("diseaseName").toString()
        predictedDiseaseAccuracy = intent.getStringExtra("diseaseAccuracy").toString()

        setContent {
            InitPredictionActivity()
        }
    }

    @Composable
    fun InitPredictionActivity() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
        ) {
            PredictionHeader()
            Spacer(Modifier.height(20.dp))
            PredictionContainer()
            PredictionDescriptionContainer()
        }
    }

    @Composable
    fun PredictionHeader() {
        val context = LocalContext.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp)
        ) {
            IconButton(onClick = {
                NavigationUtils.navigate(context, "camera", true)
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp),
                    tint = colorResource(R.color.font_color)
                )
            }

            Text(
                text = "Diagnosis", style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp
                )
            )

            // cloud icon to save prediction to firebase
            IconButton(onClick = {
                initiateUpload(context)
            }) {
                val icon =
                    if (isAlreadyUploaded.value) Icons.Default.CloudDone else Icons.Default.CloudOff
                Icon(
                    imageVector = icon,
                    contentDescription = "Upload",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        HorizontalDivider(
            color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth(.92f)
        )
    }

    @Composable
    fun PredictionContainer() {
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
                    text = "Please ensure the correctness of the Prediction",
                    style = TextStyle(
                        fontSize = 12.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.sp
                    ),
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(8.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            Image(
                bitmap = predictedImageBitmap.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .height(200.dp)
                    .border(1.dp, colorResource(R.color.font_color), RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(8.dp),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High
            )

            Text(
                text = completeLabel,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    lineHeight = 36.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
        Spacer(Modifier.height(20.dp))
    }

    @Composable
    fun PredictionDescriptionContainer() {
        val predictionDescription = getPredictionDetails(predictedDiseaseName)
        PredictionDescriptions("Description", predictionDescription.diseaseDescription)
        PredictionDescriptions("Cause", predictionDescription.diseaseCause)
        PredictionDescriptions("Symptoms", predictionDescription.diseaseSymptoms)
        PredictionDescriptions("Treatment", predictionDescription.diseaseTreatment)
    }

    @Composable
    fun PredictionDescriptions(title: String, description: String) {
        var isExpanded by remember { mutableStateOf(false) }

        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent, contentColor = colorResource(R.color.font_color)
            ),
            modifier = Modifier
                .fillMaxWidth(.95f)
                .padding(2.dp)
                .clickable { isExpanded = !isExpanded }) {
            Column(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(letterSpacing = 1.2.sp)
                    )
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Text(
                        description,
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

    private fun initiateUpload(context: Context) {
        if (isAlreadyUploaded.value) {
            Toast.makeText(context, "Data Already uploaded", Toast.LENGTH_SHORT).show()
            return
        }

        // prepare the  data
        val userId = FirebaseUserCredentials.getCurrentUserCredentails()?.uid ?: return
        val timestamp = Timestamp.now()
        val imageBase64 = ImageToBase64.convertImageToBase64Format(imagePath = predictedImgPath)

        // randomly generate the unique disease id
        val byteLength = 10
        val byteArray = ByteArray(byteLength)
        Random().nextBytes(byteArray)
        val randomDiseaseId = Base64.getUrlEncoder().withoutPadding().encodeToString(byteArray)

        Log.d("Disease ID", "$randomDiseaseId")
        
        val predictionData = PredictionData(
            userId = userId,
            diseaseId = randomDiseaseId,
            imageBase64String = imageBase64.toString(),
            predictedName = predictedDiseaseName,
            accuracy = predictedDiseaseAccuracy,
            timestamp = timestamp
        )

        //pass the data to the firestore store function
        FirebaseFirestoreUtils.storeToFirestore(predictData = predictionData, onDataStored = {
            isAlreadyUploaded.value = true
            Toast.makeText(context, "Uploaded to Firebase", Toast.LENGTH_SHORT).show()
        }, onError = { error ->
            Toast.makeText(context, "Upload failed: $error", Toast.LENGTH_SHORT).show()
        })
    }
}
