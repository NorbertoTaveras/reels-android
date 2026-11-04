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
import com.norbertotaveras.reels.data.local.account.dao.TmdbMovieActivityDao
import com.norbertotaveras.reels.data.local.account.entity.CachedMovieAccountStateEntity
import com.norbertotaveras.reels.data.local.account.entity.TmdbMovieActivityEntity
import com.norbertotaveras.reels.data.local.cache.dao.CachedContentDao
import com.norbertotaveras.reels.data.local.cache.entity.CachedContentStatusEntity
import com.norbertotaveras.reels.data.local.converter.ReelsTypeConverters
import com.norbertotaveras.reels.data.local.library.dao.FavoriteMovieDao
import com.norbertotaveras.reels.data.local.library.dao.SavedPersonDao
import com.norbertotaveras.reels.data.local.library.entity.FavoriteMovieEntity
import com.norbertotaveras.reels.data.local.library.entity.SavedPersonEntity
import com.norbertotaveras.reels.data.local.library.entity.SavedPersonKnownForEntity
import com.norbertotaveras.reels.data.local.movie.dao.CachedMovieDao
import com.norbertotaveras.reels.data.local.movie.entity.CachedGenreEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieCastEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieDetailsEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieListEntryEntity
import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieVideoEntity
import com.norbertotaveras.reels.data.local.movie.entity.MovieRemoteKeyEntity
import com.norbertotaveras.reels.data.local.person.entity.CachedPersonDetailsEntity
import com.norbertotaveras.reels.data.local.person.entity.CachedPersonMovieCreditEntity
import com.norbertotaveras.reels.data.local.plans.dao.MovieNightDao
import com.norbertotaveras.reels.data.local.plans.dao.TrackedReleaseDao
import com.norbertotaveras.reels.data.local.plans.entity.MovieNightEntity
import com.norbertotaveras.reels.data.local.plans.entity.TrackedReleaseEntity

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
        TmdbMovieActivityEntity::class,
        TrackedReleaseEntity::class,
        MovieNightEntity::class
    ],
    version = 8,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8)
    ]
)
@TypeConverters(ReelsTypeConverters::class)
abstract class ReelsDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun savedPersonDao(): SavedPersonDao
    abstract fun cachedMovieDao(): CachedMovieDao
    abstract fun cachedContentDao(): CachedContentDao
    abstract fun tmdbMovieActivityDao(): TmdbMovieActivityDao
    abstract fun trackedReleaseDao(): TrackedReleaseDao
    abstract fun movieNightDao(): MovieNightDao
}
