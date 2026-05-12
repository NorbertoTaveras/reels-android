package com.norbertotaveras.reels.features.cast.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.norbertotaveras.reels.model.data.PersonDetailsDataModel
import com.norbertotaveras.reels.model.data.PersonMovieCreditDataModel

class CastDetailsUiState {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var person by mutableStateOf<PersonDetailsDataModel?>(null)
    var knownFor by mutableStateOf<List<PersonMovieCreditDataModel>>(emptyList())
}
