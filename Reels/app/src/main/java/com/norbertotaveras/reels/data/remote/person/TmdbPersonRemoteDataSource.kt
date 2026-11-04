/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.person

import com.norbertotaveras.reels.data.remote.person.dto.PersonDetailsDto
import com.norbertotaveras.reels.data.remote.person.dto.PersonMovieCreditsDto

interface TmdbPersonRemoteDataSource {
    suspend fun getPersonDetails(
        personId: Int,
        language: String? = null,
    ): PersonDetailsDto

    suspend fun getPersonMovieCredits(
        personId: Int,
        language: String? = null,
    ): PersonMovieCreditsDto
}
