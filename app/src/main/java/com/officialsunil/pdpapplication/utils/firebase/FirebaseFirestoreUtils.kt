package com.officialsunil.pdpapplication.utils.firebase

/*
THIS CLASS IS USED FOR STORING THE PREDICTION RESULT AND MASTER  DISEASE
DESCRIPTION OF THE DISEASE
 */

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.officialsunil.pdpapplication.utils.PredictionData
import com.officialsunil.pdpapplication.utils.RetrievePredictionData
import kotlinx.coroutines.tasks.await


object FirebaseFirestoreUtils {
    private const val TAG = "Firebase"
    private const val DBNAME = "PdpApplication"
    private val fireDb by lazy { Firebase.firestore }

    // function to store the data to the firestore database
    fun storeToFirestore(
        predictData: PredictionData, onDataStored: () -> Unit, onError: (String) -> Unit
    ) {
        //create the instances of firebase batch
        val fireBatch = fireDb.batch()

        //creating the database reference
        val predictionDataRef =
            fireDb.collection(DBNAME).document(predictData.userId).collection("Predictions")
                .document(System.currentTimeMillis().toString())

        //check for emptyness
        if (predictData.userId.isEmpty() || predictData.diseaseId.isEmpty() || predictData.imageBase64String.isEmpty() || predictData.imageBase64String.equals(
                ByteArray(0)
            ) || predictData.predictedName.isEmpty() || predictData.accuracy.isEmpty() || predictData.timestamp.toString()
                .isEmpty()
        ) {
            onError("Incomplete Data ")
            return
        }

        try {
            // get the data
            val predictionData = mapOf(
                "userId" to predictData.userId,
                "diseaseId" to predictData.diseaseId,
                "imageBase64String" to predictData.imageBase64String,
                "predictedName" to predictData.predictedName,
                "accuracy" to predictData.accuracy,
                "timestamp" to predictData.timestamp
            )

            Log.d(TAG, predictionData.toString())

            //set the batches
            fireBatch.set(predictionDataRef, predictionData, SetOptions.merge())
            //commit the changes
            fireBatch.commit().addOnSuccessListener {
                onDataStored()
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error : ${exception.message}")
                onError("Fail To store data")
            }
        } catch (e: Exception) {
            onError("Failed To Store Data")
            Log.e(TAG, "Error : ${e.message}")
        }
    }

    // function to read all data from the firestore database using user id
    // call this function using lifecyclescope.launch method
    suspend fun fetchAllDiseaseInfo(
        userId: String, onError: (String) -> Unit
    ): RetrievePredictionData? {
        return try {
            // Fetch User Info (Single Document)
            val predictionSnapshot =
                fireDb.collection(DBNAME).document(userId).collection("Predictions")
                    .orderBy("timestamp", Query.Direction.DESCENDING) // Sort latest first
                    .get().await()

            //check for the existence of the user data
            val retrievePredictionData = predictionSnapshot.documents.mapNotNull { doc ->
                doc.data?.let {
                    PredictionData(
                        userId = it["userId"] as? String ?: "",
                        diseaseId = it["diseaseId"] as? String ?: "",
                        imageBase64String = it["imageBase64String"] as? String ?: "",
                        predictedName = it["predictedName"] as? String ?: "",
                        accuracy = it["accuracy"] as? String ?: "0.0",
                        timestamp = it["timestamp"] as? Timestamp ?: Timestamp.now()
                    )
                }
            }
            RetrievePredictionData(retrievePredictionData)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            onError("Error! Failed to retrieve data")
            null
        }
    }

    // fetch disease information based on the disease id
    suspend fun fetchDiseaseInfo(
        userId: String, diseaseId: String, onError: (String) -> Unit
    ): PredictionData? {
        return try {
            val snapshot = fireDb.collection(DBNAME).document(userId).collection("Predictions")
                .whereEqualTo("diseaseId", diseaseId).get().await()

            val doc = snapshot.documents.firstOrNull()

            if (doc != null && doc.data != null) {
                val it = doc.data!!
                PredictionData(
                    userId = it["userId"] as? String ?: "",
                    diseaseId = it["diseaseId"] as? String ?: "",
                    imageBase64String = it["imageBase64String"] as? String ?: "",
                    predictedName = it["predictedName"] as? String ?: "",
                    accuracy = it["accuracy"] as? String ?: "0.0",
                    timestamp = it["timestamp"] as? Timestamp ?: Timestamp.now()
                )
            } else {
                onError("No prediction data found")
                null
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error: ${err.message}")
            onError("Error! Failed to retrieve data")
            null
        }
    }

    // function to delete the disease information based on the provided disease id ref
    suspend fun deleteDiseaseInfo(
        userId: String,
        diseaseId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "User Id : $userId \nDisease Id : $diseaseId")
        try {
            // get the reference to the  disease snapshot
            val snapshot = fireDb.collection(DBNAME).document(userId).collection("Predictions")
                .whereEqualTo("diseaseId", diseaseId).get().await()

            // delete the documents
            for (document in snapshot.documents)
                fireDb.collection(DBNAME).document(userId).collection("Predictions").document(document.id)
                    .delete()
                    .addOnSuccessListener {
                        onSuccess()
                        Log.d(TAG, "Successfully deleted!")
                    }
                    .addOnFailureListener { e ->
                        onError("${e.message}")
                        Log.w(TAG, "Error deleting document", e)
                    }

        } catch (err: Exception) {
            Log.e(TAG, "Error: ${err.message}")
            onError("Error! Failed to Delete data")
        }
    }
}