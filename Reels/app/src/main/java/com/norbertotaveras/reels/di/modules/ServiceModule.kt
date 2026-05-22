package com.norbertotaveras.reels.di.modules

import com.google.gson.GsonBuilder
import com.norbertotaveras.reels.BuildConfig
import com.norbertotaveras.reels.data.remote.TmdbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
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
            key = BuildConfig.TMDB_API_KEY,
            token = BuildConfig.TMDB_READ_TOKEN,
            addAuth = true,
            addExpose = false,
            format = ""
        )
    }

    private fun <T> buildService(
        clazz: Class<T>,
        baseUrl: String,
        addAuth: Boolean,
        addExpose: Boolean,
        format: String,
        key: String,
        token: String,
    ): T {
        val gson = when(addExpose) {
            true -> GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
            else -> GsonBuilder().create()
        }
        val retrofit = Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder().apply {
                    this.connectTimeout(30L, TimeUnit.SECONDS)
                    this.readTimeout(30L, TimeUnit.SECONDS)
                }.addInterceptor(HttpLoggingInterceptor().apply {
                    this.level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.NONE
                        else HttpLoggingInterceptor.Level.NONE

                }).addInterceptor(Interceptor { chain ->
                    val request: Request = chain.request().newBuilder()
                        .apply {
                            if (addAuth) {
                                addHeader("accept", "application/json")
                                addHeader("Authorization", "Bearer $token")
                            }
                        }
                        .build()
                    //Logger.d("RequestURLInterceptor", "Request URL: ${request.url}")
                    chain.proceed(request)
                })
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(clazz)
    }
}
