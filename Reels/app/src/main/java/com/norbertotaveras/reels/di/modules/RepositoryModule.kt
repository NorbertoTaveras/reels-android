package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.repository.favorites.FavoriteMoviesRepository
import com.norbertotaveras.reels.repository.favorites.FavoriteMoviesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideFavoriteMoviesRepository(
        implementation: FavoriteMoviesRepositoryImpl
    ): FavoriteMoviesRepository
}
