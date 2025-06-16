package com.officialsunil.pdpapplication.viewui

import android.annotation.SuppressLint
import android.icu.text.DateFormatSymbols
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.customchart.ChartData
import com.officialsunil.pdpapplication.utils.customchart.getChartDataForMonth
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Month
import java.time.Year

class DiseaseAnalysis : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDPApplicationTheme {
                InitDiseaseAnalysisUI()
            }
        }
    }
}

// ui
@Composable
fun InitDiseaseAnalysisUI() {
    Scaffold(
        topBar = { AnalysisHeading() }, modifier = Modifier.systemBarsPadding()
    ) { innerPadding ->
        AnalysisContainer(modifier = Modifier.padding(innerPadding))
    }

}

@Composable
fun AnalysisHeading() {
    val context = LocalContext.current

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
                    NavigationUtils.navigate(context, "accountCenter")
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow Button ",
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "Analysis Section", style = TextStyle(
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
fun AnalysisContainer(modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(50.dp),
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(Color.White)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())

    ) {
        // summary section
        SummarySection()
        AnalysisSection()
        BarchartSection()
    }
}

// show the total  prediction summary
@Composable
fun SummarySection() {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth(.95f)
            .height(200.dp),
        colors = CardDefaults.cardColors(Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "My Predictions Summary", style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.18.sp,
                ), modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // circular progress bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .horizontalScroll(state = rememberScrollState())
            ) {
                // show the circular bar here
                CircularProgressBar("Healthy", .8f, Color.Green)
                CircularProgressBar("Early Blight", .6f, Color.Yellow)
                CircularProgressBar("Late Blight", .7f, Color.Red)
                CircularProgressBar("Unknown", .4f, Color.Blue)
            }
        }

    }
}

/*
    composable function to show the analysis section
    this section shows the highest level of predcition among halthy, lateblight and leaf blight and shows users
    which disease is more in crops

 */
@Composable
fun AnalysisSection() {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth(.95f)
            .height(200.dp),
        colors = CardDefaults.cardColors(Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Our Analysis", style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.18.sp,
                ), modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // image and the prediction sections
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                //image  part
                Image(
                    painter = painterResource(R.drawable.permission_rationale),
                    contentDescription = "analysis image",
                    contentScale = ContentScale.Fit,
                )

                Spacer(Modifier.width(13.dp))

                // Description text
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Early Blight ", style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.2.sp,
                        )
                    )

                    Text(
                        text = "Severe Early Blight infection detected.", style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(300),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Start,
                            letterSpacing = 0.1.sp,
                        )
                    )

                    Text(
                        text = "Affected: 20% higher than other diseases", style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(300),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Start,
                            letterSpacing = 0.1.sp,
                        )
                    )
                }
            }
        }

    }
}

/*
    barchart to show the montly prediction
 */

@Composable
fun BarchartSection() {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth(.95f)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Monthly Summary ", style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.18.sp,
                ), modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            FilterPicker()
        }
    }
}

// composable function for the  year and month selection
@Composable
fun FilterPicker() {
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val currentMonth =
        DateFormatSymbols().months[java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)]

    val radioOptions = listOf("Early_Blight", "Healthy", "Late_Blight", "Unknown")

    var selectedYear by remember { mutableStateOf(currentYear.toString()) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedLabel by remember { mutableStateOf(radioOptions[0]) }
    val years = (currentYear - 19..currentYear).map { it.toString() }   //{"2024" , "2024"}
    val months = DateFormatSymbols().months.take(12)

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        // year selection
        DropdownSelector(
            label = "Year", options = years, selectedOption = selectedYear, onOptionSelected = {
                selectedYear = it
                Log.d("Disease Analysis", selectedYear)
            })

        // month selection
        DropdownSelector(
            label = "Month", options = months, selectedOption = selectedMonth, onOptionSelected = {
                selectedMonth = it
                Log.d("Disease Analysis", selectedMonth)
            })

        // for the radio button
        Row(
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            radioOptions.forEach { label ->
                RadioButtonSelector(
                    label = label,
                    selectedOption = selectedLabel,
                    onOptionSelected = { selectedLabel = it })
            }
        }
    }
    // pass the required filters to the bar chart
    Barchart(year = selectedYear, month = selectedMonth, disease = selectedLabel)
}

// composable function for dropdown selector
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    // use the ExposedDropdown box
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(.70f)
    ) {

        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.SecondaryEditable)
                .wrapContentWidth(),
            readOnly = true,
            textStyle = TextStyle(
                fontSize = 14.sp, fontWeight = FontWeight.Normal, letterSpacing = 1.2.sp
            ),
            label = {
                Text(
                    text = label, style = MaterialTheme.typography.labelLarge
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Save",
                )
            })

        ExposedDropdownMenu(
            matchTextFieldWidth = true,
            scrollState = rememberScrollState(),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.wrapContentWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(text = option) }, onClick = {
                    onOptionSelected(option)
                    isExpanded = false
                })
            }
        }
    }
}

// composable function for radion button
@Composable
fun RadioButtonSelector(
    label: String, selectedOption: String, onOptionSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.selectableGroup()
    ) {
        RadioButton(
            selected = selectedOption == label,
            onClick = { onOptionSelected(label) },
            modifier = Modifier.padding(1.dp)
        )
        Text(
            text = label, style = MaterialTheme.typography.labelLarge
        )
    }
}

// function to  populate the circular progress bar
@SuppressLint("DefaultLocale")
@Composable
fun CircularProgressBar(
    label: String, progress: Float, // should be 0.0f to 1.0f
    color: Color
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .shadow(4.dp, shape = CircleShape)
                .background(Color.White, shape = CircleShape)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(0.85f),
                color = color,
                strokeWidth = 10.dp,
                trackColor = Color.White,
                strokeCap = StrokeCap.Butt
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.2f", progress * 100) + "%", style = TextStyle(
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray
                    )
                )
                Text(
                    text = label, style = TextStyle(
                        fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray
                    )
                )
            }
        }
    }
}

// composable function to show the barchart
@Composable
fun Barchart(year: String, month: String, disease: String) {
    val predictionData by produceState<List<ChartData>>(
        initialValue = emptyList(), year, month, disease
    ) {
        val monthIndex =
            DateFormatSymbols().months.indexOfFirst { it.equals(month, ignoreCase = true) }
        value = getChartDataForMonth(
            yearFilter = year.toInt(), monthFilter = monthIndex + 1, diseaseFileter = disease
        )
    }

    // Display the graph using predictionData
    if (predictionData.isNotEmpty()) {

        // Animation progress
        val animatedProgress = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(100)
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
            )
        }

        // draw the bar graph using the canvas wraped inside a box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(325.dp)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Canvas(
                contentDescription = "Bar chart", modifier = Modifier.fillMaxSize()
            ) {
                val barSpacing = 20.dp.toPx()
                val maxValue = predictionData.maxOfOrNull { it.predictionCount[disease] ?: 0 } ?: 1
                val barWidth =
                    (size.width - (predictionData.size + 1) * barSpacing) / predictionData.size
                val scaleY = size.height / (maxValue * 1.5f)  // extra space for labels

                // Draw y-axis
                drawLine(
                    color = Color.Gray,
                    start = Offset(50f, 0f),
                    end = Offset(50f, size.height - 50f),
                    strokeWidth = 2f
                )

                // Draw X-axis
                drawLine(
                    color = Color.Gray,
                    start = Offset(50f, size.height - 50f),
                    end = Offset(size.width, size.height - 50f),
                    strokeWidth = 2f
                )

                // Draw Y-axis labels
                val yStep = maxValue / 5
                for (i in 0..5) {
                    val value = i * yStep
                    val yPos = size.height - 50f - (value * scaleY * animatedProgress.value)

                    // Draw tick mark
                    drawLine(
                        color = Color.Gray,
                        start = Offset(45f, yPos),
                        end = Offset(50f, yPos),
                        strokeWidth = 2f
                    )

                    // Draw label
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            value.toString(), 30f, yPos + 10f, android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.RIGHT
                            })
                    }
                }

                // draw bars and x-axis labels
                predictionData.forEachIndexed { index, data ->
                    val count = data.predictionCount[disease] ?: 0
                    val barHeight = count * scaleY * animatedProgress.value
                    val x = 50f + barSpacing + index * (barWidth + barSpacing)
                    val y = size.height - 50f - barHeight

                    // draw bar
                    drawRect(
                        color = Color(0xFF3D09FF),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        style = Fill
                    )

                    // Draw value label on top of bar
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            count.toString(),
                            x + barWidth / 2,
                            y - 10f,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.CENTER
                            })
                    }

                    // Draw X-axis label (assuming data has a label property)
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            data.label,  // Replace with your actual label property
                            x + barWidth / 2, size.height - 20f, android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.CENTER
                            })
                    }
                }
            }

            // Add chart title
            Text(
                text = "Monthly Summary of $disease", style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ), modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )

            // Add Y-axis label
            Box(
                modifier = Modifier
                    .rotate(-90f)
                    .align(Alignment.CenterStart)
                    .offset(x = (-40).dp)
            ) {
                Text(
                    text = "Number of Predictions", style = TextStyle(
                        fontSize = 16.sp, fontWeight = FontWeight.Bold
                    ), textAlign = TextAlign.Center
                )
            }

            // Add X-axis label
            Text(
                text = "Categories",  // Change this to appropriate label
                style = TextStyle(
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                ), modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    } else Text("No data found.")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAnalysis() {
    InitDiseaseAnalysisUI()
}