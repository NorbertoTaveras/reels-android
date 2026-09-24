/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.di.modules

import android.content.Context
import androidx.room.Room
import com.norbertotaveras.reels.data.local.dao.CachedMovieDao
import com.norbertotaveras.reels.data.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.data.local.dao.SavedPersonDao
import com.norbertotaveras.reels.data.local.db.ReelsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideReelsDatabase(
        @ApplicationContext context: Context
    ): ReelsDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ReelsDatabase::class.java,
            name = "reels.db"
        )
            .build()
    }

    @Provides
    fun provideFavoriteMovieDao(
        database: ReelsDatabase
    ): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }

    @Provides
    fun provideSavedPersonDao(
        database: ReelsDatabase
    ): SavedPersonDao {
        return database.savedPersonDao()
    }

    @Provides
    fun provideCachedMovieDao(
        database: ReelsDatabase
    ): CachedMovieDao {
        return database.cachedMovieDao()
    }
}
