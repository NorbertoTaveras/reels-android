package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.genre.GenresResponseDto
import com.norbertotaveras.reels.model.dto.movie.MovieCreditsDto
import com.norbertotaveras.reels.model.dto.movie.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.movie.MovieVideosDto
import com.norbertotaveras.reels.model.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.model.dto.person.PersonDetailsDto
import com.norbertotaveras.reels.model.dto.person.PersonMovieCreditsDto
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

    @GET("movie/{movieId}/videos")
    suspend fun getMovieVideos(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
    ): MovieVideosDto

    @GET("movie/{movieId}/similar")
    suspend fun getSimilarMovies(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): MoviesResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
        @Query("include_adult") includeAdult: Boolean = false
    ): MoviesResponseDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") language: String?
    ): GenresResponseDto

}
