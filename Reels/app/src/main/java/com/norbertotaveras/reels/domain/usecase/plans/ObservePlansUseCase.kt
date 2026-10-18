/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.plans

import com.norbertotaveras.reels.domain.repository.plans.MovieNightRepository
import com.norbertotaveras.reels.domain.repository.plans.TrackedReleaseRepository
import javax.inject.Inject

class ObservePlansUseCase @Inject constructor(
    trackedReleaseRepository: TrackedReleaseRepository,
    movieNightRepository: MovieNightRepository,
) {
    val trackedReleases = trackedReleaseRepository.observeTrackedReleases()
    val movieNights = movieNightRepository.observeMovieNights()
}
