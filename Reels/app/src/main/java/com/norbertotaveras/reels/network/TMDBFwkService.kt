package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.MoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBFwkService {
    @GET("movie/{category}")
    suspend fun getMovies(
        @Path("category") category: String,
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): MoviesResponseDto

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
    ): MovieDetailsDto
}
