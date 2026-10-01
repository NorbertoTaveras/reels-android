/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.norbertotaveras.reels.data.local.dao.CachedMovieDao
import com.norbertotaveras.reels.data.local.db.ReelsDatabase
import com.norbertotaveras.reels.data.local.mapper.toMovieDataModel
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.mapper.genre.toGenreDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toDiscoverMovieRequestParameters
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieDataModel as toRemoteMovieDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieDetailsDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieVideosDataModel
import com.norbertotaveras.reels.data.remote.movie.TmdbMovieRemoteDataSource
import com.norbertotaveras.reels.data.remote.paging.CachedMovieListRemoteMediator
import com.norbertotaveras.reels.data.remote.paging.MovieCachePolicy
import com.norbertotaveras.reels.data.remote.paging.TmdbMoviesPagingSource
import com.norbertotaveras.reels.data.remote.paging.cacheKey
import com.norbertotaveras.reels.data.remote.paging.searchCacheKey
import com.norbertotaveras.reels.data.remote.paging.tmdbPagingConfig
import com.norbertotaveras.reels.data.repository.tmdbResult
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
@OptIn(ExperimentalPagingApi::class)
class TmdbMovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbMovieRemoteDataSource,
    private val database: ReelsDatabase,
    private val cachedMovieDao: CachedMovieDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MovieRepository {
    override fun getMovies(
        category: MovieCategory,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        val listKey = category.cacheKey(language = language)
        return cachedMovies(
            listKey = listKey,
            cacheTimeoutMillis = MovieCachePolicy.CATEGORY_CACHE_TIMEOUT_MILLIS,
            loadPage = { page ->
                remoteDataSource.getMovies(
                    category = category.value,
                    language = language,
                    page = page
                )
            }
        )
    }

    override fun searchMovies(
        query: String,
        language: String,
        includeAdult: Boolean,
    ): Flow<PagingData<MovieDataModel>> {
        val listKey = searchCacheKey(
            query = query,
            language = language,
            includeAdult = includeAdult
        )
        return cachedMovies(
            listKey = listKey,
            cacheTimeoutMillis = MovieCachePolicy.SEARCH_CACHE_TIMEOUT_MILLIS,
            loadPage = { page ->
                remoteDataSource.searchMovies(
                    query = query,
                    language = language,
                    page = page,
                    includeAdult = includeAdult
                )
            }
        )
    }

    override fun discoverMovies(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        val parameters = filters.toDiscoverMovieRequestParameters()
        return Pager(
            config = tmdbPagingConfig(),
            pagingSourceFactory = {
                TmdbMoviesPagingSource(
                    loadPage = { page ->
                        remoteDataSource.discoverMovies(
                            language = language,
                            page = page,
                            parameters = parameters
                        )
                    }
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { movieDto -> movieDto.toRemoteMovieDataModel() }
        }
    }

    override suspend fun getDiscoverMovieCount(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Result<Int> {
        return tmdbResult(ioDispatcher) {
            val parameters = filters.toDiscoverMovieRequestParameters()
            remoteDataSource.discoverMovies(
                language = language,
                page = 1,
                parameters = parameters
            ).totalResults
        }
    }

    override suspend fun getTrendingMovies(
        language: String,
    ): Result<List<MovieDataModel>> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getTrendingMovies(
                timeWindow = TRENDING_TIME_WINDOW,
                language = language
            ).results.map { movieDto ->
                movieDto.toRemoteMovieDataModel()
            }
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String,
    ): Result<MovieDetailsDataModel> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getMovieDetails(
                movieId = movieId,
                language = language
            ).toMovieDetailsDataModel()
        }
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String,
    ): Result<MovieCreditsDataModel> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getMovieCredits(
                movieId = movieId,
                language = language
            ).toMovieCreditsDataModel()
        }
    }

    override suspend fun getMovieVideos(
        movieId: Int,
        language: String,
    ): Result<MovieVideosDataModel> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getMovieVideos(
                movieId = movieId,
                language = language
            ).toMovieVideosDataModel()
        }
    }

    override suspend fun getSimilarMovies(
        movieId: Int,
        language: String,
        page: Int,
    ): Result<List<MovieDataModel>> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getSimilarMovies(
                movieId = movieId,
                language = language,
                page = page
            ).results.map { movieDto ->
                movieDto.toRemoteMovieDataModel()
            }
        }
    }

    override suspend fun getMovieGenres(
        language: String,
    ): Result<List<GenreDataModel>> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getMovieGenres(language = language)
                .genres
                .orEmpty()
                .map { genreDto -> genreDto.toGenreDataModel() }
        }
    }

    private fun cachedMovies(
        listKey: String,
        cacheTimeoutMillis: Long,
        loadPage: suspend (page: Int) -> MoviesResponseDto,
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = tmdbPagingConfig(),
            remoteMediator = CachedMovieListRemoteMediator(
                database = database,
                cachedMovieDao = cachedMovieDao,
                listKey = listKey,
                cacheTimeoutMillis = cacheTimeoutMillis,
                loadPage = loadPage
            ),
            pagingSourceFactory = {
                cachedMovieDao.pagingSource(listKey = listKey)
            }
        ).flow.map { pagingData ->
            pagingData.map { cachedMovie -> cachedMovie.toMovieDataModel() }
        }
    }

    private companion object {
        const val TRENDING_TIME_WINDOW = "day"
    }
}
