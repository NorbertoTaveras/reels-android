/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.repository.favorites.SavedPeopleRepository
import javax.inject.Inject

class RemoveSavedPersonUseCase @Inject constructor(
    private val savedPeopleRepository: SavedPeopleRepository
) {
    suspend operator fun invoke(personId: Int) = savedPeopleRepository.removeSavedPerson(personId)
}
