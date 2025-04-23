package com.officialsunil.pdpapplication.utils

data class DiseaseInformation(
    val diseaseId : String,
    val diseaseName: String,
    val diseaseDescription: String,
    val diseaseCause: String,
    val diseaseSymptoms: String,
    val diseaseTreatment: String
)

data class RetrieveDiseaseInformation(
    val retrieveDiseaseInformation: List<RetrieveDiseaseInformation>
)