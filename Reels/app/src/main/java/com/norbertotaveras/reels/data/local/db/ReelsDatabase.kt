/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.norbertotaveras.reels.data.local.dao.CachedContentDao
import com.norbertotaveras.reels.data.local.dao.CachedMovieDao
import com.norbertotaveras.reels.data.local.converter.ReelsTypeConverters
import com.norbertotaveras.reels.data.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.data.local.dao.SavedPersonDao
import com.norbertotaveras.reels.data.local.dao.TmdbMovieActivityDao
import com.norbertotaveras.reels.data.local.entity.CachedGenreEntity
import com.norbertotaveras.reels.data.local.entity.CachedContentStatusEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieCastEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieAccountStateEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieDetailsEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieListEntryEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieVideoEntity
import com.norbertotaveras.reels.data.local.entity.CachedPersonDetailsEntity
import com.norbertotaveras.reels.data.local.entity.CachedPersonMovieCreditEntity
import com.norbertotaveras.reels.data.local.entity.FavoriteMovieEntity
import com.norbertotaveras.reels.data.local.entity.MovieRemoteKeyEntity
import com.norbertotaveras.reels.data.local.entity.SavedPersonEntity
import com.norbertotaveras.reels.data.local.entity.SavedPersonKnownForEntity
import com.norbertotaveras.reels.data.local.entity.TmdbMovieActivityEntity

@Database(
    entities = [
        FavoriteMovieEntity::class,
        SavedPersonEntity::class,
        SavedPersonKnownForEntity::class,
        CachedMovieEntity::class,
        CachedMovieListEntryEntity::class,
        MovieRemoteKeyEntity::class,
        CachedMovieDetailsEntity::class,
        CachedMovieCastEntity::class,
        CachedMovieVideoEntity::class,
        CachedPersonDetailsEntity::class,
        CachedPersonMovieCreditEntity::class,
        CachedGenreEntity::class,
        CachedContentStatusEntity::class,
        CachedMovieAccountStateEntity::class,
        TmdbMovieActivityEntity::class
    ],
    version = 7,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7)
    ]
)
@TypeConverters(ReelsTypeConverters::class)
abstract class ReelsDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun savedPersonDao(): SavedPersonDao
    abstract fun cachedMovieDao(): CachedMovieDao
    abstract fun cachedContentDao(): CachedContentDao
    abstract fun tmdbMovieActivityDao(): TmdbMovieActivityDao
}
