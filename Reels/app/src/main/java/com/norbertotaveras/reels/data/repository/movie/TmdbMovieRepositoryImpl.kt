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
import androidx.room.withTransaction
import com.norbertotaveras.reels.data.local.cache.CachedContentType
import com.norbertotaveras.reels.data.local.cache.movieDetailsCacheKey
import com.norbertotaveras.reels.data.local.dao.CachedContentDao
import com.norbertotaveras.reels.data.local.dao.CachedMovieDao
import com.norbertotaveras.reels.data.local.db.ReelsDatabase
import com.norbertotaveras.reels.data.local.entity.CachedMovieListEntryEntity
import com.norbertotaveras.reels.data.local.entity.CachedContentStatusEntity
import com.norbertotaveras.reels.data.local.entity.MovieRemoteKeyEntity
import com.norbertotaveras.reels.data.local.mapper.toCachedGenreEntity
import com.norbertotaveras.reels.data.local.mapper.toCachedMovieCastEntities
import com.norbertotaveras.reels.data.local.mapper.toCachedMovieDetailsEntity
import com.norbertotaveras.reels.data.local.mapper.toCachedMovieVideoEntities
import com.norbertotaveras.reels.data.local.mapper.toGenreDataModel as toCachedGenreDataModel
import com.norbertotaveras.reels.data.local.mapper.toMovieCreditsDataModel as toCachedMovieCreditsDataModel
import com.norbertotaveras.reels.data.local.mapper.toMovieDetailsDataModel as toCachedMovieDetailsDataModel
import com.norbertotaveras.reels.data.local.mapper.toMovieVideosDataModel as toCachedMovieVideosDataModel
import com.norbertotaveras.reels.data.mapper.movie.toCachedMovieDataModel
import com.norbertotaveras.reels.data.mapper.movie.toCachedMovieEntity
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.mapper.genre.toGenreDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toDiscoverMovieRequestParameters
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieDetailsDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieVideosDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toTmdbPathSegment
import com.norbertotaveras.reels.data.remote.movie.TmdbMovieRemoteDataSource
import com.norbertotaveras.reels.data.remote.paging.CachedMovieListRemoteMediator
import com.norbertotaveras.reels.data.remote.paging.MovieCachePolicy
import com.norbertotaveras.reels.data.remote.paging.TMDB_FIRST_PAGE
import com.norbertotaveras.reels.data.remote.paging.cacheKey
import com.norbertotaveras.reels.data.remote.paging.discoverCacheKey
import com.norbertotaveras.reels.data.remote.paging.searchCacheKey
import com.norbertotaveras.reels.data.remote.paging.similarMoviesCacheKey
import com.norbertotaveras.reels.data.remote.paging.tmdbPagingConfig
import com.norbertotaveras.reels.data.remote.paging.trendingCacheKey
import com.norbertotaveras.reels.data.repository.cachedTmdbResult
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
    private val cachedContentDao: CachedContentDao,
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
                    category = category.toTmdbPathSegment(),
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
        return cachedMovies(
            listKey = discoverCacheKey(filters = filters, language = language),
            cacheTimeoutMillis = MovieCachePolicy.DISCOVER_CACHE_TIMEOUT_MILLIS,
            loadPage = { page ->
                remoteDataSource.discoverMovies(
                    language = language,
                    page = page,
                    parameters = parameters
                )
            }
        )
    }

    override suspend fun getDiscoverMovieCount(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Result<Int> {
        val listKey = discoverCacheKey(filters = filters, language = language)
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            loadRemote = {
                remoteDataSource.discoverMovies(
                    language = language,
                    page = TMDB_FIRST_PAGE,
                    parameters = filters.toDiscoverMovieRequestParameters()
                ).totalResults
            },
            saveRemote = { totalResults ->
                val current = cachedMovieDao.getRemoteKey(listKey = listKey)
                if (current != null) {
                    cachedMovieDao.upsertRemoteKey(
                        remoteKey = current.copy(
                            totalResults = totalResults
                        )
                    )
                }
            },
            loadCached = {
                cachedMovieDao.getRemoteKey(listKey = listKey)
                    ?.totalResults
                    ?.takeIf { count -> count > 0 }
            }
        )
    }

    override suspend fun getTrendingMovies(
        language: String,
    ): Result<List<MovieDataModel>> {
        return cachedMovieSnapshot(
            listKey = trendingCacheKey(language = language),
            loadRemote = {
                remoteDataSource.getTrendingMovies(
                    timeWindow = TRENDING_TIME_WINDOW,
                    language = language
                ).results.map { movieDto ->
                    movieDto.toMovieDataModel()
                }
            }
        )
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String,
    ): Result<MovieDetailsDataModel> {
        val cacheKey = movieDetailsCacheKey(movieId = movieId, language = language)
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            loadRemote = {
                remoteDataSource.getMovieDetails(
                    movieId = movieId,
                    language = language
                ).toMovieDetailsDataModel()
            },
            saveRemote = { movie ->
                cachedContentDao.upsertMovieDetails(
                    movie.toCachedMovieDetailsEntity(
                        cacheKey = cacheKey,
                        cachedAt = System.currentTimeMillis()
                    )
                )
            },
            loadCached = {
                cachedContentDao.getMovieDetails(cacheKey = cacheKey)
                    ?.toCachedMovieDetailsDataModel()
            }
        )
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String,
    ): Result<MovieCreditsDataModel> {
        val cacheKey = movieDetailsCacheKey(movieId = movieId, language = language)
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            loadRemote = {
                remoteDataSource.getMovieCredits(
                    movieId = movieId,
                    language = language
                ).toMovieCreditsDataModel()
            },
            saveRemote = { credits ->
                val now = System.currentTimeMillis()
                database.withTransaction {
                    cachedContentDao.clearMovieCast(cacheKey = cacheKey)
                    cachedContentDao.upsertMovieCast(
                        credits.toCachedMovieCastEntities(
                            cacheKey = cacheKey,
                            cachedAt = now
                        )
                    )
                    cachedContentDao.upsertContentStatus(
                        CachedContentStatusEntity(
                            cacheKey = cacheKey,
                            contentType = CachedContentType.MOVIE_CAST,
                            cachedAt = now
                        )
                    )
                }
            },
            loadCached = {
                if (cachedContentDao.hasContent(cacheKey, CachedContentType.MOVIE_CAST)) {
                    cachedContentDao.getMovieCast(cacheKey = cacheKey)
                        .toCachedMovieCreditsDataModel(movieId = movieId)
                } else {
                    null
                }
            }
        )
    }

    override suspend fun getMovieVideos(
        movieId: Int,
        language: String,
    ): Result<MovieVideosDataModel> {
        val cacheKey = movieDetailsCacheKey(movieId = movieId, language = language)
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            loadRemote = {
                remoteDataSource.getMovieVideos(
                    movieId = movieId,
                    language = language
                ).toMovieVideosDataModel()
            },
            saveRemote = { videos ->
                val now = System.currentTimeMillis()
                database.withTransaction {
                    cachedContentDao.clearMovieVideos(cacheKey = cacheKey)
                    cachedContentDao.upsertMovieVideos(
                        videos.toCachedMovieVideoEntities(
                            cacheKey = cacheKey,
                            cachedAt = now
                        )
                    )
                    cachedContentDao.upsertContentStatus(
                        CachedContentStatusEntity(
                            cacheKey = cacheKey,
                            contentType = CachedContentType.MOVIE_VIDEOS,
                            cachedAt = now
                        )
                    )
                }
            },
            loadCached = {
                if (cachedContentDao.hasContent(cacheKey, CachedContentType.MOVIE_VIDEOS)) {
                    cachedContentDao.getMovieVideos(cacheKey = cacheKey)
                        .toCachedMovieVideosDataModel()
                } else {
                    null
                }
            }
        )
    }

    override suspend fun getSimilarMovies(
        movieId: Int,
        language: String,
    ): Result<List<MovieDataModel>> {
        return cachedMovieSnapshot(
            listKey = similarMoviesCacheKey(movieId = movieId, language = language),
            loadRemote = {
                remoteDataSource.getSimilarMovies(
                    movieId = movieId,
                    language = language,
                    page = TMDB_FIRST_PAGE
                ).results.map { movieDto ->
                    movieDto.toMovieDataModel()
                }
            }
        )
    }

    override suspend fun getMovieGenres(
        language: String,
    ): Result<List<GenreDataModel>> {
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            loadRemote = {
                remoteDataSource.getMovieGenres(language = language)
                    .genres
                    .orEmpty()
                    .map { genreDto -> genreDto.toGenreDataModel() }
            },
            saveRemote = { genres ->
                database.withTransaction {
                    cachedContentDao.clearGenres(language = language)
                    cachedContentDao.upsertGenres(
                        genres.map { genre ->
                            genre.toCachedGenreEntity(
                                language = language,
                                cachedAt = System.currentTimeMillis()
                            )
                        }
                    )
                }
            },
            loadCached = {
                cachedContentDao.getGenres(language = language)
                    .takeIf { genres -> genres.isNotEmpty() }
                    ?.map { genre -> genre.toCachedGenreDataModel() }
            }
        )
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
            pagingData.map { cachedMovie -> cachedMovie.toCachedMovieDataModel() }
        }
    }

    private suspend fun cachedMovieSnapshot(
        listKey: String,
        loadRemote: suspend () -> List<MovieDataModel>,
    ): Result<List<MovieDataModel>> {
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            loadRemote = loadRemote,
            saveRemote = { movies ->
                val now = System.currentTimeMillis()
                database.withTransaction {
                    cachedMovieDao.clearListEntries(listKey = listKey)
                    cachedMovieDao.upsertMovies(
                        movies = movies.map { movie -> movie.toCachedMovieEntity(cachedAt = now) }
                    )
                    cachedMovieDao.upsertListEntries(
                        entries = movies.mapIndexed { index, movie ->
                            CachedMovieListEntryEntity(
                                listKey = listKey,
                                movieId = movie.id,
                                position = index
                            )
                        }
                    )
                    cachedMovieDao.upsertRemoteKey(
                        remoteKey = MovieRemoteKeyEntity(
                            listKey = listKey,
                            nextPage = null,
                            lastUpdatedAt = now,
                            totalResults = movies.size
                        )
                    )
                }
            },
            loadCached = {
                cachedMovieDao.getRemoteKey(listKey = listKey)?.let {
                    cachedMovieDao.getMovies(listKey = listKey)
                        .map { movie -> movie.toCachedMovieDataModel() }
                }
            }
        )
    }

    private companion object {
        const val TRENDING_TIME_WINDOW = "day"
    }
}
