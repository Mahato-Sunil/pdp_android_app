/*
    This class contains the  definition for the chart data class, getter methods to  populate the charts data

 */
package com.officialsunil.pdpapplication.utils.customchart

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.officialsunil.pdpapplication.utils.RetrievePredictionData
import com.officialsunil.pdpapplication.utils.firebase.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.firebase.FirebaseUserCredentials
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale

data class PredictionSummary(
    val label: String, val score: Float
)

data class ChartData(
    val label: String, val weekLabel: String, val predictionCount: Map<String, Int>
)

/* function to get the chart data

Steps :
1   => get the user credentials from the firebase
2   => get the complete prediction data from the firebase
3   => populate the chartData class with the data
4   => return the list of Chart Data

*/
suspend fun getChartDataForMonth(
    yearFilter: Int, monthFilter: Int, diseaseFileter: String
): List<ChartData> {
    val isDataAvailable = mutableStateOf(true)
    val userId = FirebaseUserCredentials.getCurrentUserCredentails()

    val predictionData: RetrievePredictionData? = FirebaseFirestoreUtils.fetchAllDiseaseInfo(
        userId = userId?.uid.toString(), onError = {
            isDataAvailable.value = false
        })

    if (!isDataAvailable.value) return emptyList()

    // extract the data
    val retrievedData = predictionData?.retrievePredictionData ?: emptyList()

    val weekFields = WeekFields.of(Locale.getDefault())

    // Step 1: Filter data by month and year
    val filteredPredictions = retrievedData.filter {
        val date =
            Instant.ofEpochSecond(it.timestamp.seconds).atZone(ZoneId.systemDefault()).toLocalDate()
        date.year == yearFilter && date.monthValue == monthFilter && it.predictedName.equals(
            diseaseFileter, ignoreCase = true
        )
    }

    // Step 2: Group by week within the filtered month
    val groupedByWeek = filteredPredictions.groupBy {
        val date =
            Instant.ofEpochSecond(it.timestamp.seconds).atZone(ZoneId.systemDefault()).toLocalDate()
        val weekNumber = date.get(weekFields.weekOfWeekBasedYear())
        "Week $weekNumber"
    }

    // Step 3: Build ChartData
    val chartDataList = groupedByWeek.map { (weekLabel, weeklyPredictions) ->
        val diseaseCount = weeklyPredictions.groupingBy { it.predictedName }.eachCount()
        ChartData(
            label = diseaseFileter, weekLabel = weekLabel, predictionCount = diseaseCount
        )
    }
    Log.d("ChartData", chartDataList.toString())
    return chartDataList.sortedBy { it.weekLabel }
}

/*
    funtion to get the total score and disease l
    label from the data base

    Steps   :
    1   => get the user credentials
    2   => get the total predictions
    3   => count the total predictions for each disease or label
    4   =>  return the list of prediction summary
 */

suspend fun getCompletePredictionSummary(): List<PredictionSummary> {
    val isDataAvailable = mutableStateOf(true)
    val userId = FirebaseUserCredentials.getCurrentUserCredentails()

    val predictionData: RetrievePredictionData? = FirebaseFirestoreUtils.fetchAllDiseaseInfo(
        userId = userId?.uid.toString(), onError = {
            isDataAvailable.value = false
        })

    if (!isDataAvailable.value) return emptyList()
    // extract the data
    val retrievedData = predictionData?.retrievePredictionData ?: emptyList()

    // Group by label and count
    val total = retrievedData.size.toFloat()

    val groupedCounts = retrievedData.groupingBy { it.predictedName }.eachCount()

    // Convert to percentage and build the summary
    val summaryList = groupedCounts.map { (label, count) ->
        val percentage = (count / total) * 100
        PredictionSummary(
            label = label, score = percentage
        )
    }

    Log.d("ChartData", summaryList.toString())
    return summaryList
}

// function to get the analysis result based on the above getCompletePredictionSummary
suspend fun getStatistics(filter: Int): PredictionSummary {
    val summaryList = getCompletePredictionSummary()
    val sortedPrediction =
        summaryList.filter { it.label.lowercase() != "unknown" }.sortedByDescending { it.score }

    val highestPrediction = when (filter) {
        0 -> sortedPrediction.getOrNull(0)
        1 -> sortedPrediction.getOrNull(1)
        else -> emptyList<PredictionSummary>()
    }

    Log.d("ChartData", highestPrediction.toString())
    return highestPrediction as PredictionSummary
}