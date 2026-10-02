/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.repository.movie

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
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
        language: String,
        includeAdult: Boolean = false,
    ): Flow<PagingData<MovieDataModel>>

    fun discoverMovies(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Flow<PagingData<MovieDataModel>>

    suspend fun getDiscoverMovieCount(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Result<Int>

    suspend fun getTrendingMovies(
        language: String,
    ): Result<List<MovieDataModel>>

    suspend fun getMovieDetails(
        movieId: Int,
        language: String,
    ): Result<MovieDetailsDataModel>

    suspend fun getMovieCredits(
        movieId: Int,
        language: String,
    ): Result<MovieCreditsDataModel>

    suspend fun getMovieVideos(
        movieId: Int,
        language: String,
    ): Result<MovieVideosDataModel>

    suspend fun getSimilarMovies(
        movieId: Int,
        language: String,
    ): Result<List<MovieDataModel>>

    suspend fun getMovieGenres(
        language: String,
    ): Result<List<GenreDataModel>>
}
