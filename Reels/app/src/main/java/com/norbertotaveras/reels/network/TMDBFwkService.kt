package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.MovieCreditsDto
import com.norbertotaveras.reels.model.dto.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.MoviesResponseDto
import com.norbertotaveras.reels.model.dto.PersonDetailsDto
import com.norbertotaveras.reels.model.dto.PersonMovieCreditsDto
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

    @GET("movie/{movieId}/credits")
    suspend fun getMovieCredits(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
    ): MovieCreditsDto

    @GET("person/{personId}")
    suspend fun getPersonDetails(
        @Path("personId") personId: Int,
        @Query("language") language: String?,
    ): PersonDetailsDto

    @GET("person/{personId}/movie_credits")
    suspend fun getPersonMovieCredits(
        @Path("personId") personId: Int,
        @Query("language") language: String?,
    ): PersonMovieCreditsDto

}
