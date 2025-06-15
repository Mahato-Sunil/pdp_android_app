/*
    This class contains the  definition for the chart data class, getter methtods to  populate the charts data

 */
package com.officialsunil.pdpapplication.utils.customchart

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.officialsunil.pdpapplication.utils.PredictionData
import com.officialsunil.pdpapplication.utils.RetrievePredictionData
import com.officialsunil.pdpapplication.utils.firebase.FirebaseFirestoreUtils
import com.officialsunil.pdpapplication.utils.firebase.FirebaseUserCredentials
import java.time.temporal.WeekFields
import java.util.Locale
import java.time.Instant
import java.time.ZoneId


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
    yearFilter: Int,
    monthFilter: Int,
    diseaseFileter : String
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
        val date =Instant.ofEpochSecond(it.timestamp.seconds).atZone(ZoneId.systemDefault()).toLocalDate()
        date.year == yearFilter &&  date.monthValue == monthFilter&& it.predictedName.equals(diseaseFileter, ignoreCase = true)
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