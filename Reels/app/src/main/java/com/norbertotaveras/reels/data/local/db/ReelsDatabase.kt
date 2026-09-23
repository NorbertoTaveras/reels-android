/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.norbertotaveras.reels.data.local.converter.ReelsTypeConverters
import com.norbertotaveras.reels.data.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.data.local.dao.SavedPersonDao
import com.norbertotaveras.reels.data.local.entity.FavoriteMovieEntity
import com.norbertotaveras.reels.data.local.entity.SavedPersonEntity
import com.norbertotaveras.reels.data.local.entity.SavedPersonKnownForEntity

@Database(
    entities = [
        FavoriteMovieEntity::class,
        SavedPersonEntity::class,
        SavedPersonKnownForEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(ReelsTypeConverters::class)
abstract class ReelsDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun savedPersonDao(): SavedPersonDao
}
