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
import com.norbertotaveras.reels.data.local.entity.TmdbMovieActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TmdbMovieActivityDao {
    @Query(
        """
        SELECT * FROM tmdb_movie_activities
        WHERE accountId = :accountId
        """
    )
    fun observeActivities(accountId: Int): Flow<List<TmdbMovieActivityEntity>>

    @Upsert
    suspend fun upsertActivity(activity: TmdbMovieActivityEntity)

    @Query(
        """
        DELETE FROM tmdb_movie_activities
        WHERE accountId = :accountId
            AND listType = :listType
            AND movieId = :movieId
        """
    )
    suspend fun deleteActivity(
        accountId: Int,
        listType: String,
        movieId: Int,
    )
}
