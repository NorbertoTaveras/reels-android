/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.settings.model

import com.norbertotaveras.reels.domain.discover.model.DiscoverFilterDataModel

data class AppSettings(
    val imageQuality: ImageQuality = ImageQuality.High,
    val languageRegion: LanguageRegion = LanguageRegion.Default,
    val theme: AppTheme = AppTheme.System,
    val recentSearches: List<String> = emptyList(),
    val discoverFilters: DiscoverFilterDataModel = DiscoverFilterDataModel(),
    val planPreferences: PlanPreferences = PlanPreferences(),
)
