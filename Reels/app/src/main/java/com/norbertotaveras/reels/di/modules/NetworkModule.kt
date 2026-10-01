/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.remote.account.TmdbAccountRemoteDataSource
import com.norbertotaveras.reels.data.remote.account.TmdbAccountRemoteDataSourceImpl
import com.norbertotaveras.reels.data.remote.movie.TmdbMovieRemoteDataSource
import com.norbertotaveras.reels.data.remote.movie.TmdbMovieRemoteDataSourceImpl
import com.norbertotaveras.reels.data.remote.person.TmdbPersonRemoteDataSource
import com.norbertotaveras.reels.data.remote.person.TmdbPersonRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindTmdbMovieRemoteDataSource(
        implementation: TmdbMovieRemoteDataSourceImpl,
    ): TmdbMovieRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTmdbPersonRemoteDataSource(
        implementation: TmdbPersonRemoteDataSourceImpl,
    ): TmdbPersonRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTmdbAccountRemoteDataSource(
        implementation: TmdbAccountRemoteDataSourceImpl,
    ): TmdbAccountRemoteDataSource
}
