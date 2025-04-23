package com.officialsunil.pdpapplication.model

import com.officialsunil.pdpapplication.utils.DiseaseInformation

//describe the disease info here

fun getPredictionDetails(prediction: String): DiseaseInformation {
    //clean the prediction string
    val cleanPrediction = prediction
        .replace(Regex("""\s*\(.*?\)\s*"""), "") // removes anything like "(12%)"
        .trim()
        .lowercase()

    return when (cleanPrediction) {
        "early_blight" -> DiseaseInformation(
            diseaseId = "23sdaf",
            diseaseName = "Early Blight",
            diseaseDescription = "A fungal disease that affects the leaves and stems.",
            diseaseCause = "Caused by the fungus *Alternaria solani*.",
            diseaseSymptoms = "Dark spots with concentric rings on older leaves.",
            diseaseTreatment = "Use fungicides and crop rotation."
        )

        // Add other conditions for different predictions...
        else -> DiseaseInformation(
            diseaseId = "23sdaf",
            diseaseName = "Unknown",
            diseaseDescription = "No information available.",
            diseaseCause = "-",
            diseaseSymptoms = "-",
            diseaseTreatment = "-"
        )
    }
}
