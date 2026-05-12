package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.MovieCreditsDto
import com.norbertotaveras.reels.model.dto.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.MoviesResponseDto
import com.norbertotaveras.reels.model.dto.PersonDetailsDto
import com.norbertotaveras.reels.model.dto.PersonMovieCreditsDto

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

    suspend fun getMovieCredits(
        movieId: Int,
        language: String? = null
    ): MovieCreditsDto

    suspend fun getPersonDetails(
        personId: Int,
        language: String? = null
    ): PersonDetailsDto

    suspend fun getPersonMovieCredits(
        personId: Int,
        language: String? = null
    ): PersonMovieCreditsDto

}
