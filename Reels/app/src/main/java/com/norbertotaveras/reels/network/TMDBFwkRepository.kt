package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.genre.GenresResponseDto
import com.norbertotaveras.reels.model.dto.movie.MovieCreditsDto
import com.norbertotaveras.reels.model.dto.movie.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.movie.MovieVideosDto
import com.norbertotaveras.reels.model.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.model.dto.person.PersonDetailsDto
import com.norbertotaveras.reels.model.dto.person.PersonMovieCreditsDto

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

    suspend fun getMovieVideos(
        movieId: Int,
        language: String? = null
    ): MovieVideosDto

    suspend fun getSimilarMovies(
        movieId: Int,
        language: String? = null,
        page: Int? = null
    ): MoviesResponseDto

    suspend fun searchMovies(
        query: String,
        language: String? = null,
        page: Int? = null,
        includeAdult: Boolean = false
    ): MoviesResponseDto

    suspend fun getMovieGenres(
        language: String? = null
    ): GenresResponseDto

}
