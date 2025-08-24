/*
    This class contains the UI and logics for
    Disease Analysis Section

    It shows the Graphs, Charts and Analysis Results
    of the total predictions made.

 */
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officialsunil.pdpapplication.R
import com.officialsunil.pdpapplication.utils.NavigationUtils
import com.officialsunil.pdpapplication.utils.customchart.ChartData
import com.officialsunil.pdpapplication.utils.customchart.PredictionSummary
import com.officialsunil.pdpapplication.utils.customchart.getChartDataForMonth
import com.officialsunil.pdpapplication.utils.customchart.getCompletePredictionSummary
import com.officialsunil.pdpapplication.utils.customchart.getStatistics
import com.officialsunil.pdpapplication.viewui.ui.theme.PDPApplicationTheme
import kotlinx.coroutines.delay


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
    var predictionSummary: List<PredictionSummary> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        predictionSummary = getCompletePredictionSummary()
    }

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

                predictionSummary.forEach { data ->
                    // choose the color
                    val color = when (data.label) {
                        "Healthy" -> Color.Green
                        "Late_Blight" -> Color.Red
                        "Early_Blight" -> Color.Yellow
                        else -> Color.Blue

                    }
                    CircularProgressBar(data.label, data.score, color)
                }
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
    var highestProbability by remember { mutableStateOf(PredictionSummary("Default", 0f)) }
    var secondHighestProbability by remember { mutableStateOf(PredictionSummary("Default", 0f)) }
    LaunchedEffect(Unit) {
        highestProbability = getStatistics(0)
        secondHighestProbability = getStatistics(1)
    }

    /*
      Now the data is in 'highestProbability' (i.e. the top prediction).

      ✅ Customization Plan:
      1. For **Main Description** based on the highest label:
          - "Early_Blight"  =>  "Several Early Blight infections detected."
          - "Late_Blight"   =>  "Severe Late Blight infection detected!!"
          - "Healthy"       =>  "Hurray! No signs of severe infections."
          - "Unknown"       =>  "Condition unknown. Further analysis recommended."

      2. For **Comparison Message** based on the second highest label and score:
          - Compare `highest.score - second.score`, generate message as:
              If label is "Early_Blight" or "Late_Blight":
                  - 70 to 100   => "Severely affected — X% higher than <second.label>"
                  - 40 to 69    => "Moderately affected — X% higher than <second.label>"
                  - 0 to 39     => "Mildly affected — X% higher than <second.label>"
              (No comparison message if label is "Healthy" or "Unknown")

      3. For **Color Code** (used in UI theme, chart, etc):
          - "Early_Blight" => Amber   (#F59E0B)
          - "Late_Blight"  => Red     (#EF4444)
          - "Healthy"      => Green   (#10B981)
          - "Unknown"      => Gray    (#9CA3AF)

      4. For **Image Resource** (used in UI preview):
          - "Early_Blight" => R.drawable.early_blight
          - "Late_Blight"  => R.drawable.late_blight
          - "Healthy"      => R.drawable.healthy
          - "Unknown"      => R.drawable.unknown
  */

    val mainDescription = when (highestProbability.label) {
        "Early_Blight" -> "Several Early Blight infections detected."
        "Late_Blight" -> "Severe Late Blight infection detected!!"
        "Healthy" -> "Hurray! No signs of severe infections."
        else -> "Condition unknown. Further analysis recommended."
    }

    val colorCode = when (highestProbability.label) {
        "Early_Blight" -> 0xFFF59E0B.toInt() // amber
        "Late_Blight" -> 0xFFEF4444.toInt() // red
        "Healthy" -> 0xFF10B981.toInt()     // green
        else -> 0xFF9CA3AF.toInt()          // gray
    }


    val imageRes = when (highestProbability.label) {
        "Early_Blight" -> R.drawable.early_blight
        "Late_Blight" -> R.drawable.late_blight
        "Healthy" -> R.drawable.healthy
        else -> R.drawable.no_image
    }

    val scoreDiff = highestProbability.score - secondHighestProbability.score

    val comparisonMsg = when (highestProbability.label) {
        "Early_Blight" -> when (scoreDiff) {
            in 70f..100f -> "Severely affected — ${scoreDiff.toInt()}% higher than ${secondHighestProbability.label}."
            in 40f..69f -> "Moderately affected — ${scoreDiff.toInt()}% higher than ${secondHighestProbability.label}."
            in 0f..39f -> "Mildly affected — ${scoreDiff.toInt()}% higher than ${secondHighestProbability.label}."
            else -> ""
        }

        "Late_Blight" -> when (scoreDiff) {
            in 70f..100f -> "Severely affected — ${scoreDiff.toInt()}% higher than ${secondHighestProbability.label}."
            in 40f..69f -> "Moderately affected — ${scoreDiff.toInt()}% higher than ${secondHighestProbability.label}."
            in 0f..39f -> "Mildly affected — ${scoreDiff.toInt()}% higher than ${secondHighestProbability.label}."
            else -> ""
        }

        else -> ""
    }

    Log.d("ChartData", highestProbability.toString())

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
                    painter = painterResource(imageRes),
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
                        text = highestProbability.label, style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            color = Color(colorCode),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.2.sp,
                        )
                    )

                    Text(
                        text = mainDescription, style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(300),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Start,
                            letterSpacing = 0.1.sp,
                        )
                    )

                    Text(
                        text = comparisonMsg, style = TextStyle(
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)
    ) {
        // pass the required filters to the bar chart
        Barchart(year = selectedYear, month = selectedMonth, disease = selectedLabel)
    }
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
                progress = { progress / 100f },
                modifier = Modifier.fillMaxSize(0.85f),
                color = color,
                strokeWidth = 10.dp,
                trackColor = Color.White,
                strokeCap = StrokeCap.Butt
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.2f", progress) + "%", style = TextStyle(
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
fun Barchart(
    year: String, month: String, disease: String
) {
    val containerHeight = 350.dp
    // for the animation and data
    val predictionData by produceState<List<ChartData>>(
        initialValue = emptyList(), year, month, disease
    ) {
        val monthIndex =
            DateFormatSymbols().months.indexOfFirst { it.equals(month, ignoreCase = true) }
        value = getChartDataForMonth(
            yearFilter = year.toInt(), monthFilter = monthIndex + 1, diseaseFileter = disease
        )
    }

    if (predictionData.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight)
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Text(
                text = "No Data Found",
            )
        }
        return
    }

    // extract the heights and percentage based on the given data
    val totalCount = predictionData.sumOf { it.predictionCount.values.sum() }.toFloat()
    val maxBarHeight = 340f
    if (totalCount > 0f) {
        val (heights, percentages) = predictionData.map { data ->
            val count = data.predictionCount.values.sum()
            val percent = (count / totalCount) * 100f
            val height = (percent / 100f) * maxBarHeight
            Pair(height, "${"%.1f".format(percent)}%")
        }.unzip()
        chartSkeleton(containerHeight, heights, percentages, disease)
    }
}

// composable function for the actual chart
@Composable
fun chartSkeleton(
    containerHeight: Dp,
    heights: List<Float>,
    percentages: List<String>,
    disease: String
) {
    // variables declaration
    val scaleGap = 35.dp
    val containerWidth = 300.dp
    val barWidth = scaleGap
    val barSpacing = scaleGap * 2
    val strokeWidth = 2f
    val tickLength = 25f
    val baseOffsetX = 30.dp
    val baseOffsetY = containerHeight / 2 + 120.dp

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(100)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
            .background(MaterialTheme.colorScheme.inverseOnSurface)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val offsetX = baseOffsetX.toPx()
            val offsetY = baseOffsetY.toPx()
            val pxScaleGap = scaleGap.toPx()
            val pxBarWidth = barWidth.toPx()

            // Y-Axis
            drawLine(
                color = Color.Blue,
                start = Offset(offsetX, 100f),
                end = Offset(offsetX, offsetY),
                strokeWidth = strokeWidth
            )

            var currentY = 100f
            while (currentY <= offsetY) {
                drawLine(
                    color = Color.Black,
                    start = Offset(offsetX - tickLength / 2, currentY),
                    end = Offset(offsetX + tickLength / 2, currentY),
                    strokeWidth = strokeWidth
                )
                currentY += pxScaleGap
            }

            // X-Axis & Ticks
            val xEnd = containerWidth.toPx() + offsetX - 120f
            var xAxisY = offsetY

            drawLine(
                color = Color.Blue,
                start = Offset(offsetX, xAxisY),
                end = Offset(xEnd, xAxisY),
                strokeWidth = strokeWidth
            )

            var currentX = offsetX
            while (currentX <= xEnd) {
                drawLine(
                    color = Color.Black,
                    start = Offset(currentX, xAxisY - 7.5f),
                    end = Offset(currentX, xAxisY + 7.5f),
                    strokeWidth = strokeWidth
                )
                currentX += pxScaleGap
            }
            xAxisY -= 96f

            // Bars
            var currentBarX = offsetX + pxScaleGap
            heights.forEachIndexed { index, barHeight ->
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(currentBarX, offsetY - pxScaleGap / 2 - barHeight + 35f),
                    size = Size(pxBarWidth, barHeight * animatedProgress.value),
                    style = Fill
                )
                currentBarX += barSpacing.toPx()
            }
        }

        // Y-Axis Label
        Text(
            text = "Predictions",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .rotate(-90f)
                .offset(x = (-150).dp, y = (-135).dp)
        )

        // Title
        Text(
            text = disease,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 10.dp)
        )

        // X-Axis Label
        Text(
            text = "Weeks",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 310.dp)
        )

        // Percentages on top of bars
        var barOffsetX = baseOffsetX + scaleGap
        heights.forEachIndexed { index, barHeight ->
            Text(
                text = percentages.getOrElse(index) { "?" },
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(
                    x = barOffsetX, y = baseOffsetY + scaleGap / 2 - barHeight.dp / 2 - 40.dp
                )
            )
            barOffsetX += barSpacing
        }
    }
}