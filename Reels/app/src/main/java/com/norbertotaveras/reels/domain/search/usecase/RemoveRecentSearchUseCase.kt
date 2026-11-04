/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.search.usecase

import com.norbertotaveras.reels.domain.settings.repository.SettingsRepository
import javax.inject.Inject

class RemoveRecentSearchUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(
        query: String,
        recentSearches: List<String>,
    ): List<String> {
        val updatedSearches = recentSearches.filterNot { recentSearch ->
            recentSearch.equals(query, ignoreCase = true)
        }
        repository.updateRecentSearches(recentSearches = updatedSearches)
        return updatedSearches
    }
}
