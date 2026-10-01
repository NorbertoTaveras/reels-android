/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.search

import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject

class ClearRecentSearchesUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(): List<String> {
        val recentSearches = emptyList<String>()
        repository.updateRecentSearches(recentSearches = recentSearches)
        return recentSearches
    }
}
