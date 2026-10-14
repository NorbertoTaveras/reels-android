/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object TmdbAccountPreferenceKeys {
    val SESSION_ID = stringPreferencesKey("session_id")
    val PENDING_REQUEST_TOKEN = stringPreferencesKey("pending_request_token")
    val ACCOUNT_ID = intPreferencesKey("account_id")
    val USERNAME = stringPreferencesKey("username")
    val DISPLAY_NAME = stringPreferencesKey("display_name")
    val AVATAR_PATH = stringPreferencesKey("avatar_path")
    val GRAVATAR_HASH = stringPreferencesKey("gravatar_hash")
    val WATCHLIST_SYNC_ENABLED = booleanPreferencesKey("watchlist_sync_enabled")
    val RATINGS_SYNC_ENABLED = booleanPreferencesKey("ratings_sync_enabled")
}
