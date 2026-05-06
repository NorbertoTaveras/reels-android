package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.MoviesResponseDto

interface TMDBFwkRepository {
    suspend fun getMovies(
        category: String,
        language: String? = null,
        page: Int? = null
    ): MoviesResponseDto

    suspend fun getMovieDetails(
        movieId: Int,
        language: String? = null
    ): MovieDetailsDto
}
