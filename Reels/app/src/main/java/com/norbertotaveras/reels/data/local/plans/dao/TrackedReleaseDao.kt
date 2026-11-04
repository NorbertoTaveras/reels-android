/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.plans.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.norbertotaveras.reels.data.local.plans.entity.TrackedReleaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedReleaseDao {
    @Query("SELECT * FROM tracked_releases ORDER BY CASE WHEN releaseDate IS NULL THEN 1 ELSE 0 END, releaseDate, title")
    fun observeAll(): Flow<List<TrackedReleaseEntity>>

    @Query("SELECT * FROM tracked_releases WHERE movieId = :movieId")
    fun observe(movieId: Int): Flow<TrackedReleaseEntity?>

    @Query("SELECT * FROM tracked_releases ORDER BY releaseDate")
    suspend fun getAll(): List<TrackedReleaseEntity>

    @Query("SELECT * FROM tracked_releases WHERE movieId = :movieId")
    suspend fun get(movieId: Int): TrackedReleaseEntity?

    @Upsert
    suspend fun upsert(entity: TrackedReleaseEntity)

    @Query("DELETE FROM tracked_releases WHERE movieId = :movieId")
    suspend fun delete(movieId: Int)
}
