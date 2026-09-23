/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.repository.person

import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditsDataModel

interface PersonRepository {
    suspend fun getPersonDetails(
        personId: Int,
        language: String = "en-US",
    ): Result<PersonDetailsDataModel>

    suspend fun getPersonMovieCredits(
        personId: Int,
        language: String = "en-US",
    ): Result<PersonMovieCreditsDataModel>
}
