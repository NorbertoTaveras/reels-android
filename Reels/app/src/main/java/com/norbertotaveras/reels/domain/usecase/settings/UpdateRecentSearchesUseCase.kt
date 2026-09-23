/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.settings

import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject

class UpdateRecentSearchesUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(recentSearches: List<String>) {
        repository.updateRecentSearches(recentSearches = recentSearches)
    }
}
