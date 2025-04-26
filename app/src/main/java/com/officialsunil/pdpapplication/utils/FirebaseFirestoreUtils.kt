package com.officialsunil.pdpapplication.utils

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
import kotlinx.coroutines.tasks.await


object FirebaseFirestoreUtils {
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

        //check for empty ness
        if (predictData.userId.isEmpty() || predictData.imageListArray.isEmpty() || predictData.imageListArray.equals(
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
                "imageListArray" to predictData.imageListArray,
                "predictedName" to predictData.predictedName,
                "accuracy" to predictData.accuracy,
                "timestamp" to predictData.timestamp
            )

            Log.d("Firestore Data", predictionData.toString())

            //set the batches
            fireBatch.set(predictionDataRef, predictionData, SetOptions.merge())
            //commit the changes
            fireBatch.commit().addOnSuccessListener {
                onDataStored()
            }.addOnFailureListener { exception ->
                onError("Fail To store data")
            }
        } catch (e: Exception) {
            onError("Failed To Store Data")
            Log.e("Firestore", "Error : ${e.message}")
        }

    }

    // function to read the data from the firestore database
    // call this function using lifecyclescope.launch method
    suspend fun readPredictionData(
        userId: String, onError: (String) -> Unit
    ): RetrievePredictionData? {
        Log.d("User Credentials", userId)
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
                        imageListArray = it["imageListArray"] as? List<Int> ?: emptyList(),
                        predictedName = it["predictedName"] as? String ?: "",
                        accuracy = it["accuracy"] as? String ?: "0.0",
                        timestamp = it["timestamp"] as? Timestamp ?: Timestamp.now() // Correctly cast to Timestamp
                    )
                }
            }

            RetrievePredictionData(retrievePredictionData)
        } catch (e: Exception) {
            Log.e("Firestore DB", "Error: ${e.message}")
            onError("Error! Failed to retrieve data")
            null
        }
    }

//
//    // function to store the family data to the firebase
//    fun storeFamilyContactData(
//        userInfo: UserInfo,
//        familyContactInfo: FamilyContactInfo,
//        onDataStored: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        //create the instances of firebase batch
//        val fireBatch = fireDb.batch()
//
//        //creating the database reference
//        val familyContactRef =
//            fireDb.collection(DBNAME).document(userInfo.userId).collection("Family")
//                .document(System.currentTimeMillis().toString())
//
//        //try to store the data
//        try {
//
//            val contactData = mapOf(
//                "name" to familyContactInfo.name,
//                "phone" to familyContactInfo.phone,
//                "email" to familyContactInfo.email,
//            )
//
//            //set the batches
//            fireBatch.set(familyContactRef, contactData, SetOptions.merge())
//
//            //commit the changes
//            fireBatch.commit().addOnSuccessListener {
//                onDataStored()
//            }.addOnFailureListener { exception ->
//                onError("Fail To store data")
//            }
//        } catch (e: Exception) {
//            onError("Failed To Store Data")
//            Log.e("Firestore", "Error : ${e.message}")
//        }
//    }
//
//    // function to retrieve the stored data
//    suspend fun readFamilyContact(
//        userId: String, onError: (String) -> Unit
//    ): RetrieveFamilyContact? {
//        return try {
////                // Fetch User Info (Single Document)
//            val userSnapshot = fireDb.collection(DBNAME).document(userId).get().await()
//
//            // Fetch Location Info (Multiple Documents)
//            val familyContactSnapshot =
//                fireDb.collection(DBNAME).document(userId).collection("Family").get().await()
//
//            if (userSnapshot.exists()) {
//                // Extract multiple locations
//                val retrieveFamilyContactData =
//                    familyContactSnapshot.documents.mapNotNull { doc ->
//                        doc.data?.let {
//                            FamilyContactInfo(
//                                name = it["name"] as? String ?: "Not Found",
//                                phone = it["phone"] as? String ?: "Not Found",
//                                email = it["email"] as? String ?: "Not Found"
//                            )
//                        }
//                    }
//
//                Log.d(
//                    "Firestore DB", "Data Retrieved: \n Family: $retrieveFamilyContactData"
//                )
//
//                RetrieveFamilyContact(retrieveFamilyContactData)
//            } else {
//                Log.e("Firestore DB", "User document not found")
//                onError("User document not found")
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("Firestore DB", "Error: ${e.message}")
//            onError("Error! Failed to retrieve data")
//            null
//        }
//    }
//
//    // function to delete the family data
//    fun deleteFamilyData(
//        userId: String,
//        contactName: String,
//        contactNumber: String,
//        onSuccess: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        // database reference
//        val familyContactRef = fireDb.collection(DBNAME).document(userId).collection("Family")
//
//        familyContactRef.whereEqualTo("name", contactName).whereEqualTo("phone", contactNumber)
//            .get().addOnSuccessListener { dbSnapshot ->
//                if (!dbSnapshot.isEmpty) {
//                    for (doc in dbSnapshot.documents) doc.reference.delete()
//                        .addOnSuccessListener {
//                            onSuccess()
//                            Log.d("Family", "Document Snapshot successfully deleted!")
//                        }
//
//                        .addOnFailureListener { e ->
//                            onError("Failed To Delete Data : " + e)
//                            Log.e("Family", "Error deleting document", e)
//                        }
//                } else onError("No Matching Record Found")
//            }
//
//            .addOnFailureListener { e ->
//                onError("Failed To Delete Data : " + e)
//                Log.e("Family", "Error deleting document", e)
//            }
//    }
//
//    //function to store the sos message details to firebase
//    fun setupSosMessage(
//        userInfo: UserInfo,
//        messageContents: MessageContents,
//        onMessageStored: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        val fireBatch = fireDb.batch()
//        val sosMessageRef =
//            fireDb.collection(DBNAME).document(userInfo.userId).collection("SOS")
//                .document("Message_Contents")
//
//        try {
//            val messageData = mapOf(
//                "messageFormat" to messageContents.messageFormat,
//                "recipient" to messageContents.recipient
//            )
//
//            fireBatch.set(sosMessageRef, messageData, SetOptions.merge())
//            fireBatch.commit().addOnSuccessListener {
//                onMessageStored()
//            }.addOnFailureListener { exception ->
//                onError("Fail To store message")
//            }
//        } catch (e: Exception) {
//            onError("Failed To Store Data")
//            Log.e("Firestore", "Error : ${e.message}")
//        }
//    }
//
//    suspend fun readSosMessage(
//        userId: String, onError: (String) -> Unit
//    ): RetrieveMessageContents? {
//        return try {
//            val userSnapshot = fireDb.collection(DBNAME).document(userId).get().await()
//
//            val messageContentsSnapshot =
//                fireDb.collection(DBNAME).document(userId).collection("SOS")
//                    .document("Message_Contents").get().await()
//
//            if (userSnapshot.exists()) {
//                val retrieveMessageContents =
//                    messageContentsSnapshot.data?.let {
//                        MessageContents(
//                            messageFormat = it["messageFormat"] as? String ?: "Emergency !",
//                            recipient = it["recipient"] as? String
//                                ?: "9860650642"     //default author number
//                        )
//                    }
//                RetrieveMessageContents(listOfNotNull(retrieveMessageContents))
//            } else {
//                onError("User document not found")
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("Firestore DB", "Error: ${e.message}")
//            onError("Error! Failed to retrieve data")
//            null
//        }
//    }
//}
}