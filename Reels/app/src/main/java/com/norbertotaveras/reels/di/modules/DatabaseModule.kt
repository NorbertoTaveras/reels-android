package com.norbertotaveras.reels.di.modules

import android.content.Context
import androidx.room.Room
import com.norbertotaveras.reels.model.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.model.local.db.ReelsDatabase
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
        ).build()
    }

    @Provides
    fun provideFavoriteMovieDao(
        database: ReelsDatabase
    ): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }
}
