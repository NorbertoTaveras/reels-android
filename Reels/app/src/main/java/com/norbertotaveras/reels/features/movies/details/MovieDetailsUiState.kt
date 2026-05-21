package com.norbertotaveras.reels.features.movies.details

import com.norbertotaveras.reels.model.data.cast.CastMemberDataModel
import com.norbertotaveras.reels.model.data.movie.MovieDataModel
import com.norbertotaveras.reels.model.data.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.model.data.movie.MovieVideoDataModel

data class MovieDetailsUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetailsDataModel? = null,
    val cast: List<CastMemberDataModel> = emptyList(),
    val trailers: List<MovieVideoDataModel> = emptyList(),
    val similarMovies: List<MovieDataModel> = emptyList(),
    val isFavorite: Boolean = false,
    val isFavoriteLoading: Boolean = false,
    val isOfflineFavorite: Boolean = false,
    val errorMessage: String? = null
)
