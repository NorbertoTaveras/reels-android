package com.norbertotaveras.reels.model.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.norbertotaveras.reels.model.local.converter.ReelsTypeConverters
import com.norbertotaveras.reels.model.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.model.local.entity.FavoriteMovieEntity

@Database(
    entities = [
        FavoriteMovieEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ReelsTypeConverters::class)
abstract class ReelsDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}
