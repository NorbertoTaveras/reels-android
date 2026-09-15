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
