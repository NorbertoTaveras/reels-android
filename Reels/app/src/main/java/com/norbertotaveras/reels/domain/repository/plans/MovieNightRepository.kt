/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.repository.plans

import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import kotlinx.coroutines.flow.Flow

interface MovieNightRepository {
    fun observeMovieNights(): Flow<List<MovieNightPlan>>
    fun observeMovieNight(id: String): Flow<MovieNightPlan?>
    fun observeUpcomingMovieNight(movieId: Int): Flow<MovieNightPlan?>
    suspend fun getMovieNights(): List<MovieNightPlan>
    suspend fun schedule(plan: MovieNightPlan): Result<Unit>
    suspend fun edit(plan: MovieNightPlan): Result<Unit>
    suspend fun markStartingSoon(id: String): Result<Unit>
    suspend fun markMissed(id: String): Result<Unit>
    suspend fun start(id: String): Result<Unit>
    suspend fun pause(id: String): Result<Unit>
    suspend fun resume(id: String): Result<Unit>
    suspend fun finish(id: String): Result<Unit>
    suspend fun cancel(id: String): Result<Unit>
    suspend fun remove(id: String): Result<Unit>
}
