package com.norbertotaveras.reels.network

import com.norbertotaveras.reels.model.dto.MovieDetailsDto
import com.norbertotaveras.reels.model.dto.MoviesResponseDto
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
}
