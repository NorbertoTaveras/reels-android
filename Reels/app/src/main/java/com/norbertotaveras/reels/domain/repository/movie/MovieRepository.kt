package com.norbertotaveras.reels.domain.repository.movie

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(
        category: MovieCategory,
        language: String,
    ): Flow<PagingData<MovieDataModel>>

    fun searchMovies(
        query: String,
        language: String = "en-US",
        includeAdult: Boolean = false,
    ): Flow<PagingData<MovieDataModel>>

    suspend fun getMovieDetails(
        movieId: Int,
        language: String = "en-US",
    ): Result<MovieDetailsDataModel>

    suspend fun getMovieCredits(
        movieId: Int,
        language: String = "en-US",
    ): Result<MovieCreditsDataModel>

    suspend fun getMovieVideos(
        movieId: Int,
        language: String = "en-US",
    ): Result<MovieVideosDataModel>

    suspend fun getSimilarMovies(
        movieId: Int,
        language: String = "en-US",
        page: Int = 1,
    ): Result<List<MovieDataModel>>

    suspend fun getMovieGenres(
        language: String = "en-US",
    ): Result<List<GenreDataModel>>
}
