/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import androidx.datastore.preferences.core.stringPreferencesKey

internal object SettingsPreferenceKeys {
    val IMAGE_QUALITY = stringPreferencesKey("image_quality")
    val LANGUAGE_REGION = stringPreferencesKey("language_region")
    val THEME = stringPreferencesKey("theme")
    val RECENT_SEARCHES = stringPreferencesKey("recent_searches")
    val DISCOVER_FILTERS = stringPreferencesKey("discover_filters")
}
