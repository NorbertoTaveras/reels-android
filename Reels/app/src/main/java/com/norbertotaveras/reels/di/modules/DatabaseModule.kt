package com.norbertotaveras.reels.di.modules

import android.content.Context
import androidx.room.Room
import com.norbertotaveras.reels.data.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.data.local.db.ReelsDatabase
import com.norbertotaveras.reels.data.local.db.ReelsDatabaseMigrations
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
            .addMigrations(*ReelsDatabaseMigrations.all)
            .build()
    }

    @Provides
    fun provideFavoriteMovieDao(
        database: ReelsDatabase
    ): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }
}
