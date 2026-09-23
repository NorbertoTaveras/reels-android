/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.di.modules

import com.google.gson.GsonBuilder
import com.norbertotaveras.reels.BuildConfig
import com.norbertotaveras.reels.data.remote.TmdbService
import com.norbertotaveras.reels.data.remote.error.TmdbConfigurationException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @get:Provides
    val tmdbService: TmdbService by lazy {
        buildService(
            clazz = TmdbService::class.java,
            baseUrl = BuildConfig.BASE_URL,
            token = BuildConfig.TMDB_READ_TOKEN,
            addAuth = true,
            addExpose = false
        )
    }

    private fun <T> buildService(
        clazz: Class<T>,
        baseUrl: String,
        addAuth: Boolean,
        addExpose: Boolean,
        token: String,
    ): T {
        val gson = when (addExpose) {
            true -> GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
            else -> GsonBuilder().create()
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.LOG_NETWORK) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val retrofit = Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder().apply {
                    this.connectTimeout(30L, TimeUnit.SECONDS)
                    this.readTimeout(30L, TimeUnit.SECONDS)
                }
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(tmdbAuthInterceptor(addAuth = addAuth, token = token))
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(clazz)
    }

    private fun tmdbAuthInterceptor(
        addAuth: Boolean,
        token: String,
    ): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            if (addAuth) {
                if (token.isBlank()) {
                    throw TmdbConfigurationException(
                        message = "TMDB API token is missing. Add TMDB_READ_TOKEN to local.properties, an environment variable, or a Gradle property."
                    )
                }

                requestBuilder
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
    }
}
