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
import com.norbertotaveras.reels.data.local.mapper.toCachedMovieEntity
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import com.norbertotaveras.reels.domain.model.constant.MovieCategory

@OptIn(ExperimentalPagingApi::class)
class CachedMoviesRemoteMediator(
    private val database: ReelsDatabase,
    private val cachedMovieDao: CachedMovieDao,
    private val remoteDataSource: TmdbRemoteDataSource,
    private val category: MovieCategory,
    private val language: String,
) : RemoteMediator<Int, CachedMovieEntity>() {
    private val listKey = category.cacheKey(language = language)

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CachedMovieEntity>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> FIRST_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    cachedMovieDao.getRemoteKey(listKey = listKey)?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = remoteDataSource.getMovies(
                category = category.value,
                language = language,
                page = page
            )
            val now = System.currentTimeMillis()
            val endOfPaginationReached = page >= response.totalPages
            val listStartPosition = (page - 1) * state.config.pageSize

            database.withTransaction {
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
                            position = listStartPosition + index
                        )
                    }
                )
                cachedMovieDao.upsertRemoteKey(
                    remoteKey = MovieRemoteKeyEntity(
                        listKey = listKey,
                        nextPage = if (endOfPaginationReached) null else page + 1,
                        lastUpdatedAt = now
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (error: Exception) {
            MediatorResult.Error(error.toTmdbNetworkException())
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}

fun MovieCategory.cacheKey(language: String): String {
    return "category:$value:language:$language"
}
