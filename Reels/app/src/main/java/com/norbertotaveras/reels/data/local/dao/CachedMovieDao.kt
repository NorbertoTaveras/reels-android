/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.norbertotaveras.reels.data.local.entity.CachedMovieEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieListEntryEntity
import com.norbertotaveras.reels.data.local.entity.MovieRemoteKeyEntity

@Dao
interface CachedMovieDao {
    @Query(
        """
        SELECT cached_movies.* FROM cached_movies
        INNER JOIN cached_movie_list_entries
            ON cached_movies.id = cached_movie_list_entries.movieId
        WHERE cached_movie_list_entries.listKey = :listKey
        ORDER BY cached_movie_list_entries.position ASC
        """
    )
    fun pagingSource(listKey: String): PagingSource<Int, CachedMovieEntity>

    @Query("SELECT * FROM movie_remote_keys WHERE listKey = :listKey LIMIT 1")
    suspend fun getRemoteKey(listKey: String): MovieRemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovies(movies: List<CachedMovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertListEntries(entries: List<CachedMovieListEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRemoteKey(remoteKey: MovieRemoteKeyEntity)

    @Query("DELETE FROM cached_movie_list_entries WHERE listKey = :listKey")
    suspend fun clearListEntries(listKey: String)

    @Query("DELETE FROM movie_remote_keys WHERE listKey = :listKey")
    suspend fun clearRemoteKey(listKey: String)
}
