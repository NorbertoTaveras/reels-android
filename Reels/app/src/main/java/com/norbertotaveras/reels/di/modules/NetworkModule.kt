package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSourceImpl
import com.norbertotaveras.reels.data.remote.TMDBFwkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideTmdbRemoteDataSource(service: TMDBFwkService): TmdbRemoteDataSource {
        return TmdbRemoteDataSourceImpl(service)
    }
}