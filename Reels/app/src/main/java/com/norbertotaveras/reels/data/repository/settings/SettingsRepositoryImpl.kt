/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.settings

import com.norbertotaveras.reels.data.local.preferences.SettingsDataStore
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SettingsRepository {
    override fun observeSettings(): Flow<AppSettings> {
        return settingsDataStore.settings.flowOn(ioDispatcher)
    }

    override suspend fun updateImageQuality(imageQuality: ImageQuality) {
        withContext(ioDispatcher) {
            settingsDataStore.updateImageQuality(imageQuality = imageQuality)
        }
    }

    override suspend fun updateLanguageRegion(languageRegion: LanguageRegion) {
        withContext(ioDispatcher) {
            settingsDataStore.updateLanguageRegion(languageRegion = languageRegion)
        }
    }

    override suspend fun updateTheme(theme: AppTheme) {
        withContext(ioDispatcher) {
            settingsDataStore.updateTheme(theme = theme)
        }
    }

    override suspend fun updateRecentSearches(recentSearches: List<String>) {
        withContext(ioDispatcher) {
            settingsDataStore.updateRecentSearches(recentSearches = recentSearches)
        }
    }

    override suspend fun updateDiscoverFilters(filters: DiscoverFilterDataModel) {
        withContext(ioDispatcher) {
            settingsDataStore.updateDiscoverFilters(filters = filters)
        }
    }
}
