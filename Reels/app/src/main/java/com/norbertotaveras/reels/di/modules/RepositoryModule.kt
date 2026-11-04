/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.repository.account.TmdbAccountRepositoryImpl
import com.norbertotaveras.reels.data.repository.library.FavoriteMoviesRepositoryImpl
import com.norbertotaveras.reels.data.repository.library.SavedPeopleRepositoryImpl
import com.norbertotaveras.reels.data.repository.movie.TmdbMovieRepositoryImpl
import com.norbertotaveras.reels.data.repository.person.TmdbPersonRepositoryImpl
import com.norbertotaveras.reels.data.repository.plans.MovieNightRepositoryImpl
import com.norbertotaveras.reels.data.repository.plans.TrackedReleaseRepositoryImpl
import com.norbertotaveras.reels.data.repository.settings.CoilImageCacheRepository
import com.norbertotaveras.reels.data.repository.settings.SettingsRepositoryImpl
import com.norbertotaveras.reels.domain.account.repository.TmdbAccountRepository
import com.norbertotaveras.reels.domain.library.repository.FavoriteMoviesRepository
import com.norbertotaveras.reels.domain.library.repository.SavedPeopleRepository
import com.norbertotaveras.reels.domain.movie.repository.MovieRepository
import com.norbertotaveras.reels.domain.person.repository.PersonRepository
import com.norbertotaveras.reels.domain.plans.repository.MovieNightRepository
import com.norbertotaveras.reels.domain.plans.repository.TrackedReleaseRepository
import com.norbertotaveras.reels.domain.settings.repository.ImageCacheRepository
import com.norbertotaveras.reels.domain.settings.repository.SettingsRepository
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
    abstract fun bindFavoriteMoviesRepository(
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
        implementation: TmdbMovieRepositoryImpl
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindPersonRepository(
        implementation: TmdbPersonRepositoryImpl
    ): PersonRepository

    @Binds
    @Singleton
    abstract fun bindImageCacheRepository(
        implementation: CoilImageCacheRepository
    ): ImageCacheRepository

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

    @Binds
    @Singleton
    abstract fun bindTrackedReleaseRepository(
        implementation: TrackedReleaseRepositoryImpl
    ): TrackedReleaseRepository

    @Binds
    @Singleton
    abstract fun bindMovieNightRepository(
        implementation: MovieNightRepositoryImpl
    ): MovieNightRepository
}
