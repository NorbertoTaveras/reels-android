/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.movie.usecase

import com.norbertotaveras.reels.domain.library.repository.FavoriteMoviesRepository
import com.norbertotaveras.reels.domain.movie.model.MovieDetailsContentUpdate
import com.norbertotaveras.reels.domain.movie.repository.MovieRepository
import com.norbertotaveras.reels.domain.plans.repository.TrackedReleaseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class LoadMovieDetailsContentUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val favoriteMoviesRepository: FavoriteMoviesRepository,
    private val trackedReleaseRepository: TrackedReleaseRepository,
) {
    operator fun invoke(
        movieId: Int,
        language: String,
    ): Flow<MovieDetailsContentUpdate> = channelFlow {
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
            if (offlineFavorite != null) {
                send(
                    MovieDetailsContentUpdate.Main(
                        movie = offlineFavorite.movie,
                        cachedAt = offlineFavorite.cachedAt,
                        isOfflineContent = true
                    )
                )
            } else {
                send(
                    MovieDetailsContentUpdate.Failure(
                        movieResult.exceptionOrNull()
                            ?: IllegalStateException("Movie details request failed")
                    )
                )
            }
            return@channelFlow
        }

        send(MovieDetailsContentUpdate.Main(movie, movieSource.cachedAt))

        if (movieSource.cachedAt == null) {
            launch {
                trackedReleaseRepository.refreshReleaseDate(
                    movieId = movieId,
                    releaseDate = movie.releaseDate
                )
            }
        }

        launch {
            val members = movieRepository.getMovieCredits(movieId, language).getOrNull()
                ?.cast?.take(DETAILS_SECTION_LIMIT).orEmpty()
            send(MovieDetailsContentUpdate.Cast(members))
        }
        launch {
            val videos = movieRepository.getMovieVideos(movieId, language).getOrNull()
                ?.videos?.filter { it.isPlayableTrailer }?.take(DETAILS_SECTION_LIMIT).orEmpty()
            send(MovieDetailsContentUpdate.Trailers(videos))
        }
        launch {
            val movies = movieRepository.getSimilarMovies(movieId, language).getOrNull()
                ?.take(DETAILS_SECTION_LIMIT).orEmpty()
            send(MovieDetailsContentUpdate.Similar(movies))
        }
    }

    private companion object {
        const val DETAILS_SECTION_LIMIT = 10
    }
}
