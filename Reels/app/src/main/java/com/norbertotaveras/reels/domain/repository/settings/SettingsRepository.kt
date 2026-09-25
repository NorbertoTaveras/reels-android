/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.repository.settings

import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun updateImageQuality(imageQuality: ImageQuality)
    suspend fun updateLanguageRegion(languageRegion: LanguageRegion)
    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateRecentSearches(recentSearches: List<String>)
    suspend fun updateDiscoverFilters(filters: DiscoverFilterDataModel)
}
