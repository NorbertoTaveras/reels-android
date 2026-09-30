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
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.mapper.genre.toGenreDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieDataModel as toRemoteMovieDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieDetailsDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieVideosDataModel
import com.norbertotaveras.reels.data.remote.paging.CachedMoviesRemoteMediator
import com.norbertotaveras.reels.data.remote.paging.CachedSearchMoviesRemoteMediator
import com.norbertotaveras.reels.data.remote.paging.DiscoverMoviesPagingSource
import com.norbertotaveras.reels.data.remote.paging.cacheKey
import com.norbertotaveras.reels.data.remote.paging.searchCacheKey
import com.norbertotaveras.reels.data.remote.paging.tmdbPagingConfig
import com.norbertotaveras.reels.data.remote.releaseDateFromParameter
import com.norbertotaveras.reels.data.remote.releaseDateToParameter
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
    private val remoteDataSource: TmdbRemoteDataSource,
    private val database: ReelsDatabase,
    private val cachedMovieDao: CachedMovieDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MovieRepository {
    override fun getMovies(
        category: MovieCategory,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = tmdbPagingConfig(),
            remoteMediator = CachedMoviesRemoteMediator(
                database = database,
                cachedMovieDao = cachedMovieDao,
                remoteDataSource = remoteDataSource,
                category = category,
                language = language
            ),
            pagingSourceFactory = {
                cachedMovieDao.pagingSource(listKey = category.cacheKey(language = language))
            }
        ).flow.map { pagingData ->
            pagingData.map { cachedMovie -> cachedMovie.toMovieDataModel() }
        }
    }

    override fun searchMovies(
        query: String,
        language: String,
        includeAdult: Boolean,
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = tmdbPagingConfig(),
            remoteMediator = CachedSearchMoviesRemoteMediator(
                database = database,
                cachedMovieDao = cachedMovieDao,
                remoteDataSource = remoteDataSource,
                query = query,
                language = language,
                includeAdult = includeAdult
            ),
            pagingSourceFactory = {
                cachedMovieDao.pagingSource(
                    listKey = searchCacheKey(
                        query = query,
                        language = language,
                        includeAdult = includeAdult
                    )
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { cachedMovie -> cachedMovie.toMovieDataModel() }
        }
    }

    override fun discoverMovies(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return Pager(
            config = tmdbPagingConfig(),
            pagingSourceFactory = {
                DiscoverMoviesPagingSource(
                    remoteDataSource = remoteDataSource,
                    filters = filters,
                    language = language
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
            remoteDataSource.discoverMovies(
                language = language,
                page = 1,
                sortBy = filters.sort.apiValue,
                genreId = filters.genreId,
                releaseDateFrom = filters.releaseDateFromParameter(),
                releaseDateTo = filters.releaseDateToParameter(),
                minimumRating = filters.minimumRating
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

    private companion object {
        const val TRENDING_TIME_WINDOW = "day"
    }
}
