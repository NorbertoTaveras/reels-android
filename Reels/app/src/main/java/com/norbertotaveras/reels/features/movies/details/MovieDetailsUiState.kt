package com.norbertotaveras.reels.features.movies.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.norbertotaveras.reels.model.data.cast.CastMemberDataModel
import com.norbertotaveras.reels.model.data.movie.MovieDataModel
import com.norbertotaveras.reels.model.data.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.model.data.movie.MovieVideoDataModel

class MovieDetailsUiState {
    var isLoading by mutableStateOf(true)
    var movie by mutableStateOf<MovieDetailsDataModel?>(null)
    var cast by mutableStateOf<List<CastMemberDataModel>>(emptyList())
    var trailers by mutableStateOf<List<MovieVideoDataModel>>(emptyList())
    var similarMovies by mutableStateOf<List<MovieDataModel>>(emptyList())
    var isFavorite by mutableStateOf(false)
    var isFavoriteLoading by mutableStateOf(false)
    var isOfflineFavorite by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
}
