package com.norbertotaveras.reels.features.movies.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.norbertotaveras.reels.model.data.MovieDetailsDataModel

class MovieDetailsUiState {
    var isLoading by mutableStateOf(true)
    var movie by mutableStateOf<MovieDetailsDataModel?>(null)
    var errorMessage by mutableStateOf<String?>(null)
}
