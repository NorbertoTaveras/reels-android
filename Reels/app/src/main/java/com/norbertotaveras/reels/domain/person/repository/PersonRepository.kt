/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.person.repository

import com.norbertotaveras.reels.domain.person.model.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditsDataModel

interface PersonRepository {
    suspend fun getPersonDetails(
        personId: Int,
        language: String,
    ): Result<PersonDetailsDataModel>

    suspend fun getPersonMovieCredits(
        personId: Int,
        language: String,
    ): Result<PersonMovieCreditsDataModel>
}
