/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.norbertotaveras.reels.data.local.dao.CachedMovieDao
import com.norbertotaveras.reels.data.local.db.ReelsDatabase
import com.norbertotaveras.reels.data.local.entity.CachedMovieEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieListEntryEntity
import com.norbertotaveras.reels.data.local.entity.MovieRemoteKeyEntity
import com.norbertotaveras.reels.data.mapper.movie.toCachedMovieEntity
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalPagingApi::class)
internal class CachedMovieListRemoteMediator(
    private val database: ReelsDatabase,
    private val cachedMovieDao: CachedMovieDao,
    private val listKey: String,
    private val cacheTimeoutMillis: Long,
    private val loadPage: suspend (page: Int) -> MoviesResponseDto,
    private val transformError: suspend (Exception) -> Throwable = { error ->
        error.toTmdbNetworkException()
    },
) : RemoteMediator<Int, CachedMovieEntity>() {
    override suspend fun initialize(): InitializeAction {
        if (cachedMovieDao.getListEntryCount(listKey = listKey) == 0) {
            return InitializeAction.LAUNCH_INITIAL_REFRESH
        }
        val lastUpdatedAt = cachedMovieDao.getRemoteKey(listKey = listKey)?.lastUpdatedAt
            ?: return InitializeAction.LAUNCH_INITIAL_REFRESH
        val isCacheFresh = MovieCachePolicy.isFresh(
            lastUpdatedAt = lastUpdatedAt,
            now = System.currentTimeMillis(),
            timeoutMillis = cacheTimeoutMillis
        )

        return if (isCacheFresh) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CachedMovieEntity>,
    ): MediatorResult {
        return try {
            val page = pageFor(loadType = loadType)
                ?: return MediatorResult.Success(endOfPaginationReached = true)
            val response = loadPage(page)
            val now = System.currentTimeMillis()
            val endOfPaginationReached = page >= response.totalPages
            val listStartPosition = (page - 1) * state.config.pageSize

            database.withTransaction {
                cleanupExpiredCache(now = now)

                if (loadType == LoadType.REFRESH) {
                    cachedMovieDao.clearListEntries(listKey = listKey)
                    cachedMovieDao.clearRemoteKey(listKey = listKey)
                }

                cachedMovieDao.upsertMovies(
                    movies = response.results.map { movie ->
                        movie.toCachedMovieEntity(cachedAt = now)
                    }
                )
                cachedMovieDao.upsertListEntries(
                    entries = response.results.mapIndexed { index, movie ->
                        CachedMovieListEntryEntity(
                            listKey = listKey,
                            movieId = movie.id,
                            position = listStartPosition + index,
                            accountRating = movie.accountRating
                        )
                    }
                )
                cachedMovieDao.upsertRemoteKey(
                    remoteKey = MovieRemoteKeyEntity(
                        listKey = listKey,
                        nextPage = if (endOfPaginationReached) null else page + 1,
                        lastUpdatedAt = now,
                        totalResults = response.totalResults
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            MediatorResult.Error(transformError(error))
        }
    }

    private suspend fun pageFor(loadType: LoadType): Int? {
        return when (loadType) {
            LoadType.REFRESH -> TMDB_FIRST_PAGE
            LoadType.PREPEND -> null
            LoadType.APPEND -> cachedMovieDao.getRemoteKey(listKey = listKey)?.nextPage
        }
    }

    private suspend fun cleanupExpiredCache(now: Long) {
        val cutoffMillis = MovieCachePolicy.retentionCutoff(now = now)
        cachedMovieDao.clearExpiredListEntries(cutoffMillis = cutoffMillis)
        cachedMovieDao.clearExpiredRemoteKeys(cutoffMillis = cutoffMillis)
        cachedMovieDao.clearOrphanMovies()
    }
}
