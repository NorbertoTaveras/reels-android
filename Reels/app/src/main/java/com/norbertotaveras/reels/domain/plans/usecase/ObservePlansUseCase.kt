/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans.usecase

import com.norbertotaveras.reels.domain.plans.repository.MovieNightRepository
import com.norbertotaveras.reels.domain.plans.repository.TrackedReleaseRepository
import javax.inject.Inject

class ObservePlansUseCase @Inject constructor(
    trackedReleaseRepository: TrackedReleaseRepository,
    movieNightRepository: MovieNightRepository,
) {
    val trackedReleases = trackedReleaseRepository.observeTrackedReleases()
    val movieNights = movieNightRepository.observeMovieNights()
}
