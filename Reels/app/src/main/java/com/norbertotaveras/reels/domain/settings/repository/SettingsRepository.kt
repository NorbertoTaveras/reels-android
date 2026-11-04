/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.settings.repository

import com.norbertotaveras.reels.domain.discover.model.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.settings.model.AppSettings
import com.norbertotaveras.reels.domain.settings.model.AppTheme
import com.norbertotaveras.reels.domain.settings.model.ImageQuality
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion
import com.norbertotaveras.reels.domain.settings.model.PlanPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun updateImageQuality(imageQuality: ImageQuality)
    suspend fun updateLanguageRegion(languageRegion: LanguageRegion)
    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateRecentSearches(recentSearches: List<String>)
    suspend fun updateDiscoverFilters(filters: DiscoverFilterDataModel)
    suspend fun updatePlanPreferences(preferences: PlanPreferences)
}
