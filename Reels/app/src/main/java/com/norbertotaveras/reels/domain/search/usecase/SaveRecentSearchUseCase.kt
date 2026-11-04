/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.search.usecase

import com.norbertotaveras.reels.domain.search.model.SearchQueryPolicy
import com.norbertotaveras.reels.domain.settings.repository.SettingsRepository
import javax.inject.Inject

class SaveRecentSearchUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(
        query: String,
        recentSearches: List<String>,
    ): List<String> {
        val normalizedQuery = SearchQueryPolicy.normalize(query = query)
        if (!SearchQueryPolicy.isValid(query = normalizedQuery)) return recentSearches

        val updatedSearches = buildList {
            add(normalizedQuery)
            addAll(
                recentSearches.filterNot { recentSearch ->
                    recentSearch.equals(normalizedQuery, ignoreCase = true)
                }
            )
        }.take(MAX_RECENT_SEARCHES)

        repository.updateRecentSearches(recentSearches = updatedSearches)
        return updatedSearches
    }

    private companion object {
        const val MAX_RECENT_SEARCHES = 5
    }
}
