package com.officialsunil.pdpapplication.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SQLiteDatabaseViewModel(
    private val dao: SQLiteDatabaseInterface
) : ViewModel() {
    // get the uid of current lognec users
    val currentUser = FirebaseUserCredentials.getCurrentUserCredentails()

    private val _sortType = MutableStateFlow(SortType.TIMESTAMP)
    private val _predictions = _sortType
        .flatMapLatest { sortType ->
        when (sortType) {
            SortType.NAME -> dao.getPredictionListOrderedByName(userId = currentUser?.uid ?: "")
            SortType.TIMESTAMP -> dao.getPredictionListOrderedByTimestamp(
                userId = currentUser?.uid ?: ""
            )
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    private val _state = MutableStateFlow(PredictionState())
    val state = combine (_state, _sortType, _predictions) {
        state, sortType, predictions ->
        state.copy(
            predictions = predictions,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PredictionState())

    // events
    fun onEvent(event: SQLiteDatabaseEvent) {
        when (event) {
            is SQLiteDatabaseEvent.DeletePrediction -> {
                viewModelScope.launch {
                    dao.deletePrediction(event.predictions)
                }
            }

            is SQLiteDatabaseEvent.SavePrediction -> {
                val userId = state.value.userId
                val diseaseId = state.value.diseaseId
                val name = state.value.name
                val image = state.value.image
                val accuracy = state.value.accuracy
                val timestamp = state.value.timestamp

                //check for blank value
                if(userId.isBlank() || name.isBlank() || diseaseId.isBlank() || accuracy.isBlank() || image == null || timestamp.toString().isEmpty())
                    return

                val predictionData = Predictions (
                    userId = userId,
                    diseaseId = diseaseId,
                    name = name,
                    image = image,
                    accuracy = accuracy,
                    timestamp = timestamp
                )

                viewModelScope.launch {
                    dao.insertPredicitions(predictionData)
                }

                _state.update { it.copy(
                    isStoringPredictions = true,
                    userId = "",
                    name = "",
                    diseaseId = "",
                    image = ByteArray(0),
                    accuracy = "",
                    timestamp = Timestamp.now().toString()
                ) }

            }

            is SQLiteDatabaseEvent.SetPredictedImage -> {
                _state.update {
                    it.copy(
                        image = event.predictedImage,
                    )
                }
            }

            is SQLiteDatabaseEvent.SetPredictedName -> {
                _state.update {
                    it.copy(
                        name = event.predictedName,
                    )
                }
            }

            is SQLiteDatabaseEvent.SetUserId -> {
                _state.update {
                    it.copy(
                        userId = event.userId,
                    )
                }
            }

            is SQLiteDatabaseEvent.SetDiseaseId -> {
                _state.update {
                    it.copy(
                        diseaseId = event.diseaseId,
                    )
                }
            }

            is SQLiteDatabaseEvent.SetAccuracy -> {
                _state.update {
                    it.copy(
                        accuracy = event.accuracy,
                    )
                }
            }

            is SQLiteDatabaseEvent.SetTimestamp -> {
                _state.update {
                    it.copy(
                        timestamp = event.timestamp,
                    )
                }
            }

            is SQLiteDatabaseEvent.SortPrediction -> {
                _sortType.value = event.sortType
            }
        }

    }

}