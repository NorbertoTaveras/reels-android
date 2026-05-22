package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.repository.TmdbRepositoryImpl
import com.norbertotaveras.reels.data.repository.favorites.FavoriteMoviesRepositoryImpl
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
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

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        implementation: TmdbRepositoryImpl
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindPersonRepository(
        implementation: TmdbRepositoryImpl
    ): PersonRepository
}
