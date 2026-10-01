/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.person

import com.norbertotaveras.reels.data.remote.dto.person.PersonDetailsDto
import com.norbertotaveras.reels.data.remote.dto.person.PersonMovieCreditsDto
import javax.inject.Inject

class TmdbPersonRemoteDataSourceImpl @Inject constructor(
    private val service: TmdbPersonService,
) : TmdbPersonRemoteDataSource {
    override suspend fun getPersonDetails(
        personId: Int,
        language: String?,
    ): PersonDetailsDto {
        return service.getPersonDetails(
            personId = personId,
            language = language
        )
    }

    override suspend fun getPersonMovieCredits(
        personId: Int,
        language: String?,
    ): PersonMovieCreditsDto {
        return service.getPersonMovieCredits(
            personId = personId,
            language = language
        )
    }
}
