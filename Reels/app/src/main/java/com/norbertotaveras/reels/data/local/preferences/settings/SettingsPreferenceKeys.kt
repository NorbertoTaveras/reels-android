/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object SettingsPreferenceKeys {
    val IMAGE_QUALITY = stringPreferencesKey("image_quality")
    val LANGUAGE_REGION = stringPreferencesKey("language_region")
    val THEME = stringPreferencesKey("theme")
    val RECENT_SEARCHES = stringPreferencesKey("recent_searches")
    val DISCOVER_FILTERS = stringPreferencesKey("discover_filters")
    val RELEASE_REMINDER = stringPreferencesKey("release_reminder")
    val MOVIE_NIGHT_REMINDER = intPreferencesKey("movie_night_reminder")
    val LIVE_UPDATES = booleanPreferencesKey("live_updates")
}
