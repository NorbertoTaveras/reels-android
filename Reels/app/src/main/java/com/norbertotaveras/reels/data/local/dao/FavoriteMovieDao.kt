/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.norbertotaveras.reels.data.local.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {
    @Query("SELECT * FROM favorite_movies ORDER BY favoritedAt DESC")
    fun observeFavoriteMovies(): Flow<List<FavoriteMovieEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    fun observeIsFavorite(movieId: Int): Flow<Boolean>

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId LIMIT 1")
    suspend fun getFavoriteMovie(movieId: Int): FavoriteMovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavoriteMovie(movie: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteMovie(movieId: Int)
}
