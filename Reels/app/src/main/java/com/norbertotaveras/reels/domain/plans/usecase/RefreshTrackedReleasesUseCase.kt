/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans.usecase

import com.norbertotaveras.reels.domain.movie.repository.MovieRepository
import com.norbertotaveras.reels.domain.plans.repository.TrackedReleaseRepository
import javax.inject.Inject

class RefreshTrackedReleasesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val trackedReleaseRepository: TrackedReleaseRepository,
) {
    suspend operator fun invoke(language: String) {
        trackedReleaseRepository.getTrackedReleases().forEach { trackedRelease ->
            val freshDetails = movieRepository
                .getMovieDetails(movieId = trackedRelease.movieId, language = language)
                .getOrNull()
                ?.takeIf { it.cachedAt == null }
                ?: return@forEach
            trackedReleaseRepository.refreshReleaseDate(
                movieId = trackedRelease.movieId,
                releaseDate = freshDetails.movie.releaseDate,
            )
        }
    }
}
