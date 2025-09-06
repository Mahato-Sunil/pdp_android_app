package com.officialsunil.pdpapplication.utils.firebase

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.officialsunil.pdpapplication.utils.DiseaseInformation
import com.officialsunil.pdpapplication.utils.RetrieveDiseaseInformation
import kotlinx.coroutines.tasks.await

object FirebaseDiseaseUtils {

    private const val TAG = "FirebaseDisease"
    private const val DBNAME = "PdpApplication"
    private val fireDb by lazy { Firebase.firestore }

    // ----------------- INSERT / ADD DISEASE -----------------
    fun addDisease(
        disease: DiseaseInformation,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val diseaseRef = fireDb.collection(DBNAME)
            .document("Diseases")
            .collection("DiseaseList")
            .document(disease.diseaseId)

        try {
            fireDb.runBatch { batch ->
                batch.set(diseaseRef, disease, SetOptions.merge())
            }.addOnSuccessListener {
                onSuccess()
                Log.d(TAG, "Disease added successfully")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error adding disease: ${e.message}")
                onError("Failed to add disease")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            onError("Failed to add disease")
        }
    }

    // ----------------- FETCH ALL DISEASES -----------------
    suspend fun fetchAllDiseases(onError: (String) -> Unit): List<DiseaseInformation>? {
        return try {
            val snapshot = fireDb.collection(DBNAME)
                .document("Diseases")
                .collection("DiseaseList")
                .get()
                .await()

            val diseases = snapshot.documents.mapNotNull { doc ->
                doc.toObject(DiseaseInformation::class.java)
            }

            diseases
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching diseases: ${e.message}")
            onError("Failed to fetch diseases")
            null
        }
    }

    // ----------------- FETCH SINGLE DISEASE -----------------
    suspend fun fetchDisease(
        diseaseId: String,
        onError: (String) -> Unit
    ): DiseaseInformation? {
        return try {
            val snapshot = fireDb.collection(DBNAME)
                .document("Diseases")
                .collection("DiseaseList")
                .document(diseaseId)
                .get()
                .await()

            snapshot.toObject(DiseaseInformation::class.java) ?: run {
                onError("Disease not found")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching disease: ${e.message}")
            onError("Failed to fetch disease")
            null
        }
    }

    // ----------------- UPDATE / EDIT DISEASE -----------------
    fun updateDisease(
        disease: DiseaseInformation,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val diseaseRef = fireDb.collection(DBNAME)
            .document("Diseases")
            .collection("DiseaseList")
            .document(disease.diseaseId)

        try {
            fireDb.runBatch { batch ->
                batch.set(diseaseRef, disease, SetOptions.merge())
            }.addOnSuccessListener {
                onSuccess()
                Log.d(TAG, "Disease updated successfully")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error updating disease: ${e.message}")
                onError("Failed to update disease")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            onError("Failed to update disease")
        }
    }

    // ----------------- DELETE DISEASE -----------------
    fun deleteDisease(
        diseaseId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val diseaseRef = fireDb.collection(DBNAME)
            .document("Diseases")
            .collection("DiseaseList")
            .document(diseaseId)

        diseaseRef.delete()
            .addOnSuccessListener {
                onSuccess()
                Log.d(TAG, "Disease deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting disease: ${e.message}")
                onError("Failed to delete disease")
            }
    }
}
