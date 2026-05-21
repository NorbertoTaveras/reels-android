package com.norbertotaveras.reels.di.modules

import com.norbertotaveras.reels.data.remote.TMDBFwkRepository
import com.norbertotaveras.reels.data.remote.TMDBFwkRepositoryImpl
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
    fun provideTMDBFwkRepository(service: TMDBFwkService): TMDBFwkRepository {
        return TMDBFwkRepositoryImpl(service)
    }
}