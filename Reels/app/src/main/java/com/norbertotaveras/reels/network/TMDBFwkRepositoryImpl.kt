package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.MovieCreditsDto
import com.norbertotaveras.reels.model.dto.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.MoviesResponseDto
import com.norbertotaveras.reels.model.dto.PersonDetailsDto
import com.norbertotaveras.reels.model.dto.PersonMovieCreditsDto
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
}
