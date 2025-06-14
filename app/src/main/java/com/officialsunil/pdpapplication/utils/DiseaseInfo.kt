package com.officialsunil.pdpapplication.utils

object DiseaseInfo {
    //describe the disease info here
    fun getPredictionDetails(prediction: String): DiseaseInformation {

        val cleanPrediction = prediction.replace(Regex("""\s*\(.*?\)\s*"""), "").trim().lowercase()

        return when (cleanPrediction) {
            "healthy" -> DiseaseInformation(
                diseaseId = "d001",
                diseaseName = "Healthy",
                diseaseDescription = "The potato leaf appears healthy, showing no visible signs of disease or stress.",
                diseaseCause = "No pathogens or stress factors detected.",
                diseaseSymptoms = "Uniform green color, no spots or lesions, and proper leaf structure.",
                diseaseTreatment = "No treatment required. Maintain regular crop care and monitoring."
            )

            "early_blight" -> DiseaseInformation(
                diseaseId = "d002",
                diseaseName = "Early Blight",
                diseaseDescription = "Early blight is a common fungal disease affecting potato and tomato plants, primarily during warm and humid conditions.",
                diseaseCause = "Caused by the fungus *Alternaria solani*, which thrives in warm, wet climates.",
                diseaseSymptoms = "Concentric ring patterns (target spots) on older leaves, usually with yellow halos. May also affect stems and fruits.",
                diseaseTreatment = "Apply fungicides like chlorothalonil or mancozeb. Practice crop rotation, remove infected plant debris, and ensure good field drainage."
            )

            "late_blight" -> DiseaseInformation(
                diseaseId = "d003",
                diseaseName = "Late Blight",
                diseaseDescription = "Late blight is a devastating potato disease responsible for the Irish Potato Famine, and still poses a serious threat to crops today.",
                diseaseCause = "Caused by the water mold *Phytophthora infestans*, which spreads rapidly in cool, moist environments.",
                diseaseSymptoms = "Irregular, water-soaked lesions on leaves that turn brown or black, often with a yellow edge. White mold may appear on the underside of leaves in humid conditions.",
                diseaseTreatment = "Remove and destroy infected plants. Use resistant varieties, ensure proper spacing for air circulation, and apply fungicides like metalaxyl or fluazinam."
            )

            else -> DiseaseInformation(
                diseaseId = "d999",
                diseaseName = "Unknown",
                diseaseDescription = "It looks like an unknown object and not a Potato Leaf. Make sure potato's leaf is visible in the image.",
                diseaseCause = "No Information",
                diseaseSymptoms = "No Information",
                diseaseTreatment = "No Information"
            )
        }
    }
}
