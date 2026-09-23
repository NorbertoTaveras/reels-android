/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.person

import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
import javax.inject.Inject

class GetPersonDetailsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(
        personId: Int,
        language: String = "en-US"
    ): Result<PersonDetailsDataModel> {
        return repository.getPersonDetails(
            personId = personId,
            language = language
        )
    }
}
