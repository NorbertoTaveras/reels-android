/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.model.movie.MovieDetailsContentDataModel
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadMovieDetailsContentUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val favoriteMoviesRepository: FavoriteMoviesRepository,
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String,
    ): Result<MovieDetailsContentDataModel> {
        val movieResult = movieRepository.getMovieDetails(
            movieId = movieId,
            language = language
        )
        val movieSource = movieResult.getOrNull()
        val movie = movieSource?.movie

        if (movie == null) {
            val offlineFavorite = favoriteMoviesRepository.getFavoriteMovieDetails(
                movieId = movieId
            )
            return if (offlineFavorite != null) {
                Result.success(
                    MovieDetailsContentDataModel(
                        movie = offlineFavorite.movie,
                        cast = emptyList(),
                        trailers = emptyList(),
                        similarMovies = emptyList(),
                        isOfflineContent = true,
                        cachedAt = offlineFavorite.cachedAt
                    )
                )
            } else {
                Result.failure(
                    movieResult.exceptionOrNull()
                        ?: IllegalStateException("Movie details request failed")
                )
            }
        }

        return coroutineScope {
            val creditsDeferred = async {
                movieRepository.getMovieCredits(
                    movieId = movieId,
                    language = language
                )
            }
            val videosDeferred = async {
                movieRepository.getMovieVideos(
                    movieId = movieId,
                    language = language
                )
            }
            val similarDeferred = async {
                movieRepository.getSimilarMovies(
                    movieId = movieId,
                    language = language
                )
            }

            Result.success(
                MovieDetailsContentDataModel(
                    movie = movie,
                    cast = creditsDeferred.await()
                        .getOrNull()
                        ?.cast
                        ?.take(DETAILS_SECTION_LIMIT)
                        .orEmpty(),
                    trailers = videosDeferred.await()
                        .getOrNull()
                        ?.videos
                        ?.filter { video -> video.isPlayableTrailer }
                        ?.take(DETAILS_SECTION_LIMIT)
                        .orEmpty(),
                    similarMovies = similarDeferred.await()
                        .getOrNull()
                        ?.take(DETAILS_SECTION_LIMIT)
                        .orEmpty(),
                    isOfflineContent = movieSource.cachedAt != null,
                    cachedAt = movieSource.cachedAt
                )
            )
        }
    }

    private companion object {
        const val DETAILS_SECTION_LIMIT = 10
    }
}
