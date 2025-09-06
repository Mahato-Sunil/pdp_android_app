package com.officialsunil.pdpapplication.admin.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.admin.screen.ui.theme.PDPApplicationTheme
import com.officialsunil.pdpapplication.utils.DiseaseInformation
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.firebase.FirebaseDiseaseUtils

class AdminAddDisease : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
               AdminAddDiseaseScreen()
            }
        }
    }
}
@Composable
fun AdminAddDiseaseScreen() {
    Scaffold(
        topBar = { AdminAddDiseaseHeader() },
        modifier = Modifier
            .systemBarsPadding()
            .background(colorResource(R.color.light_background))
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AdminAddDiseaseContainerUI(
                modifier = Modifier.padding(innerPadding), context = LocalContext.current
            )
        }
    }
}

// header
@Composable
fun AdminAddDiseaseHeader() {
    val context = LocalContext.current
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            IconButton(
                onClick = {
                    NavigationUtils.navigate(context, "adminCase", true)
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Arrow Back",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "Add Disease", style = TextStyle(
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
fun AdminAddDiseaseContainerUI(modifier: Modifier, context: Context) {
    var diseaseName by remember { mutableStateOf("") }
    var diseaseDescription by remember { mutableStateOf("") }
    var diseaseCause by remember { mutableStateOf("") }
    var diseaseSymptoms by remember { mutableStateOf("") }
    var diseaseTreatment by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter Disease Details",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        // Use reusable composable for input fields
        InputField("Disease Name", diseaseName, { diseaseName = it }, showError)
        InputField("Disease Description", diseaseDescription, { diseaseDescription = it }, showError)
        InputField("Disease Cause", diseaseCause, { diseaseCause = it }, showError)
        InputField("Disease Symptoms", diseaseSymptoms, { diseaseSymptoms = it }, showError)
        InputField("Disease Treatment", diseaseTreatment, { diseaseTreatment = it }, showError)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                if (diseaseName.isBlank() || diseaseDescription.isBlank() ||
                    diseaseCause.isBlank() || diseaseSymptoms.isBlank() ||
                    diseaseTreatment.isBlank()
                ) {
                    showError = true
                } else {
                    val newDisease = DiseaseInformation(
                        diseaseId = System.currentTimeMillis().toString(), // unique ID
                        diseaseName = diseaseName,
                        diseaseDescription = diseaseDescription,
                        diseaseCause = diseaseCause,
                        diseaseSymptoms = diseaseSymptoms,
                        diseaseTreatment = diseaseTreatment
                    )

                    FirebaseDiseaseUtils.addDisease(
                        newDisease,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Disease Added Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            NavigationUtils.navigate(context, "adminCase", true)
                        },
                        onError = { error ->
                            Log.e("Admin", "Error : $error")
                            Toast.makeText(context, "Failed to add Disease", Toast.LENGTH_SHORT).show()
                        }
                    )
                   }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF4CAF50)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF4CAF50)
            )
        ) {
            Text(
                text = "Add Disease",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showError: Boolean
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showError && value.isBlank(),
            textStyle = TextStyle(fontSize = 16.sp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red
            )
        )
        if (showError && value.isBlank()) {
            Text(
                text = "$label is required",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}

@Composable
@Preview
fun AdminAddDiseaseScreenPreview() {
    AdminAddDiseaseScreen()
}