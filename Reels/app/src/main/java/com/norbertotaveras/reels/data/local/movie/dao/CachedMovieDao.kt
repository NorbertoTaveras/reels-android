/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.movie.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieListEntryEntity
import com.norbertotaveras.reels.data.local.movie.entity.MovieRemoteKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedMovieDao {
    @Query(
        """
        SELECT
            cached_movies.id,
            cached_movies.title,
            cached_movies.overview,
            cached_movies.posterPath,
            cached_movies.backdropPath,
            cached_movies.releaseDate,
            cached_movies.voteAverage,
            cached_movies.genreIds,
            cached_movie_list_entries.accountRating AS accountRating,
            cached_movies.cachedAt
        FROM cached_movies
        INNER JOIN cached_movie_list_entries
            ON cached_movies.id = cached_movie_list_entries.movieId
        WHERE cached_movie_list_entries.listKey = :listKey
        ORDER BY cached_movie_list_entries.position ASC
        """
    )
    fun pagingSource(listKey: String): PagingSource<Int, CachedMovieEntity>

    @Query(
        """
        SELECT
            cached_movies.id,
            cached_movies.title,
            cached_movies.overview,
            cached_movies.posterPath,
            cached_movies.backdropPath,
            cached_movies.releaseDate,
            cached_movies.voteAverage,
            cached_movies.genreIds,
            cached_movie_list_entries.accountRating AS accountRating,
            cached_movies.cachedAt
        FROM cached_movies
        INNER JOIN cached_movie_list_entries
            ON cached_movies.id = cached_movie_list_entries.movieId
        WHERE cached_movie_list_entries.listKey = :listKey
        ORDER BY cached_movie_list_entries.position ASC
        """
    )
    suspend fun getMovies(listKey: String): List<CachedMovieEntity>

    @Query("SELECT COUNT(*) FROM cached_movie_list_entries WHERE listKey = :listKey")
    suspend fun getListEntryCount(listKey: String): Int

    @Query("SELECT * FROM movie_remote_keys WHERE listKey = :listKey LIMIT 1")
    suspend fun getRemoteKey(listKey: String): MovieRemoteKeyEntity?

    @Query("SELECT lastUpdatedAt FROM movie_remote_keys WHERE listKey = :listKey LIMIT 1")
    fun observeLastUpdatedAt(listKey: String): Flow<Long?>

    @Upsert
    suspend fun upsertMovies(movies: List<CachedMovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertListEntries(entries: List<CachedMovieListEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRemoteKey(remoteKey: MovieRemoteKeyEntity)

    @Query("DELETE FROM cached_movie_list_entries WHERE listKey = :listKey")
    suspend fun clearListEntries(listKey: String)

    @Query("DELETE FROM movie_remote_keys WHERE listKey = :listKey")
    suspend fun clearRemoteKey(listKey: String)

    @Query("DELETE FROM movie_remote_keys WHERE listKey LIKE 'account:' || :accountId || ':%'")
    suspend fun invalidateAccountLists(accountId: Int)

    @Query(
        """
        DELETE FROM cached_movie_list_entries
        WHERE movieId = :movieId
            AND listKey LIKE 'account:' || :accountId || ':' || :listType || ':%'
        """
    )
    suspend fun removeAccountListMovie(
        accountId: Int,
        listType: String,
        movieId: Int,
    )

    @Query(
        """
        DELETE FROM cached_movie_list_entries
        WHERE listKey IN (
            SELECT listKey FROM movie_remote_keys
            WHERE lastUpdatedAt < :cutoffMillis
        )
        """
    )
    suspend fun clearExpiredListEntries(cutoffMillis: Long)

    @Query("DELETE FROM movie_remote_keys WHERE lastUpdatedAt < :cutoffMillis")
    suspend fun clearExpiredRemoteKeys(cutoffMillis: Long)

    @Query(
        """
        DELETE FROM cached_movies
        WHERE id NOT IN (
            SELECT movieId FROM cached_movie_list_entries
        )
        """
    )
    suspend fun clearOrphanMovies()
}
