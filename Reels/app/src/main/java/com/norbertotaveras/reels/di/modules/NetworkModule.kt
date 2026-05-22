package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSourceImpl
import com.norbertotaveras.reels.data.remote.TmdbService
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
    fun provideTmdbRemoteDataSource(service: TmdbService): TmdbRemoteDataSource {
        return TmdbRemoteDataSourceImpl(service)
    }
}