/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.norbertotaveras.reels.data.local.entity.MovieNightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieNightDao {
    @Query("SELECT * FROM movie_nights ORDER BY scheduledAt")
    fun observeAll(): Flow<List<MovieNightEntity>>

    @Query("SELECT * FROM movie_nights WHERE id = :id")
    fun observe(id: String): Flow<MovieNightEntity?>

    @Query("SELECT * FROM movie_nights ORDER BY scheduledAt")
    suspend fun getAll(): List<MovieNightEntity>

    @Query("SELECT * FROM movie_nights WHERE id = :id")
    suspend fun get(id: String): MovieNightEntity?

    @Upsert
    suspend fun upsert(entity: MovieNightEntity)

    @Query("DELETE FROM movie_nights WHERE id = :id")
    suspend fun delete(id: String)
}
