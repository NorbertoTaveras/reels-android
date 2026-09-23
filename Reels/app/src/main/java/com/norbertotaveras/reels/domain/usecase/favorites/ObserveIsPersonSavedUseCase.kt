/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.repository.favorites.SavedPeopleRepository
import javax.inject.Inject

class ObserveIsPersonSavedUseCase @Inject constructor(
    private val savedPeopleRepository: SavedPeopleRepository
) {
    operator fun invoke(personId: Int) = savedPeopleRepository.observeIsPersonSaved(personId)
}
