package com.officialsunil.pdpapplication.model

import com.officialsunil.pdpapplication.utils.PredictionDescription

//describe the disease info here

fun getPredictionDetails(prediction: String): PredictionDescription {
    //clean the prediction string
    val cleanPrediction = prediction
        .replace(Regex("""\s*\(.*?\)\s*"""), "") // removes anything like "(12%)"
        .trim()
        .lowercase()

    return when (cleanPrediction) {
        "early_blight" -> PredictionDescription(
            diseaseName = "Early Blight",
            diseaseDescription = "A fungal disease that affects the leaves and stems.",
            diseaseCause = "Caused by the fungus *Alternaria solani*.",
            diseaseSymptoms = "Dark spots with concentric rings on older leaves.",
            diseaseTreatment = "Use fungicides and crop rotation."
        )

        // Add other conditions for different predictions...
        else -> PredictionDescription(
            diseaseName = "Unknown",
            diseaseDescription = "No information available.",
            diseaseCause = "-",
            diseaseSymptoms = "-",
            diseaseTreatment = "-"
        )
    }
}
