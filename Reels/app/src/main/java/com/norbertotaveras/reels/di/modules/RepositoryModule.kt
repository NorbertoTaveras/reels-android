/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.repository.TmdbRepositoryImpl
import com.norbertotaveras.reels.data.repository.account.TmdbAccountRepositoryImpl
import com.norbertotaveras.reels.data.repository.favorites.FavoriteMoviesRepositoryImpl
import com.norbertotaveras.reels.data.repository.favorites.SavedPeopleRepositoryImpl
import com.norbertotaveras.reels.data.repository.settings.SettingsRepositoryImpl
import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import com.norbertotaveras.reels.domain.repository.favorites.SavedPeopleRepository
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
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
    abstract fun bindSavedPeopleRepository(
        implementation: SavedPeopleRepositoryImpl
    ): SavedPeopleRepository

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

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        implementation: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindTmdbAccountRepository(
        implementation: TmdbAccountRepositoryImpl
    ): TmdbAccountRepository
}
