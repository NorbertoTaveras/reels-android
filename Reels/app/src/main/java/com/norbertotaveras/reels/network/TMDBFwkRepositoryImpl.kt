package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.genre.GenresResponseDto
import com.norbertotaveras.reels.model.dto.movie.MovieCreditsDto
import com.norbertotaveras.reels.model.dto.movie.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.movie.MovieVideosDto
import com.norbertotaveras.reels.model.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.model.dto.person.PersonDetailsDto
import com.norbertotaveras.reels.model.dto.person.PersonMovieCreditsDto
import javax.inject.Inject

class TMDBFwkRepositoryImpl @Inject constructor(
    private val service: TMDBFwkService
) : TMDBFwkRepository {
    override suspend fun getMovies(
        category: String,
        language: String?,
        page: Int?
    ): MoviesResponseDto {
        return service.getMovies(
            category = category,
            language = language,
            page = page
        )
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?
    ): MovieDetailsDto {
        return service.getMovieDetails(
            movieId = movieId,
            language = language
        )
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String?
    ): MovieCreditsDto {
        return service.getMovieCredits(
            movieId = movieId,
            language = language
        )
    }

    override suspend fun getPersonDetails(
        personId: Int,
        language: String?
    ): PersonDetailsDto {
        return service.getPersonDetails(
            personId = personId,
            language = language
        )
    }

    override suspend fun getPersonMovieCredits(
        personId: Int,
        language: String?
    ): PersonMovieCreditsDto {
        return service.getPersonMovieCredits(
            personId = personId,
            language = language
        )
    }

    override suspend fun getMovieVideos(
        movieId: Int,
        language: String?
    ): MovieVideosDto {
        return service.getMovieVideos(
            movieId = movieId,
            language = language
        )
    }

    override suspend fun getSimilarMovies(
        movieId: Int,
        language: String?,
        page: Int?
    ): MoviesResponseDto {
        return service.getSimilarMovies(
            movieId = movieId,
            language = language,
            page = page
        )
    }

    override suspend fun searchMovies(
        query: String,
        language: String?,
        page: Int?,
        includeAdult: Boolean
    ): MoviesResponseDto {
        return service.searchMovies(
            query = query,
            language = language,
            page = page,
            includeAdult = includeAdult
        )
    }

    override suspend fun getMovieGenres(
        language: String?
    ): GenresResponseDto {
        return service.getMovieGenres(
            language = language
        )
    }
}
