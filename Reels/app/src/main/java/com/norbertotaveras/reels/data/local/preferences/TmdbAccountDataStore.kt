/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.norbertotaveras.reels.domain.model.account.TmdbAccountDataModel
import com.norbertotaveras.reels.domain.model.account.TmdbAccountStateDataModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tmdbAccountDataStore by preferencesDataStore(name = "tmdb_account")

@Singleton
class TmdbAccountDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val accountState: Flow<TmdbAccountStateDataModel> = context.tmdbAccountDataStore.data.map { preferences ->
        preferences.toTmdbAccountState()
    }

    suspend fun savePendingRequestToken(requestToken: String) {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences[TmdbAccountPreferenceKeys.PENDING_REQUEST_TOKEN] = requestToken
        }
    }

    suspend fun saveConnectedAccount(
        sessionId: String,
        account: TmdbAccountDataModel,
    ) {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences[TmdbAccountPreferenceKeys.SESSION_ID] = sessionId
            preferences[TmdbAccountPreferenceKeys.ACCOUNT_ID] = account.accountId
            preferences[TmdbAccountPreferenceKeys.USERNAME] = account.username
            account.displayName
                ?.takeIf { displayName -> displayName.isNotBlank() }
                ?.let { displayName ->
                    preferences[TmdbAccountPreferenceKeys.DISPLAY_NAME] = displayName
                }
                ?: preferences.remove(TmdbAccountPreferenceKeys.DISPLAY_NAME)
            account.avatarPath
                ?.takeIf { avatarPath -> avatarPath.isNotBlank() }
                ?.let { avatarPath ->
                    preferences[TmdbAccountPreferenceKeys.AVATAR_PATH] = avatarPath
                }
                ?: preferences.remove(TmdbAccountPreferenceKeys.AVATAR_PATH)
            account.gravatarHash
                ?.takeIf { gravatarHash -> gravatarHash.isNotBlank() }
                ?.let { gravatarHash ->
                    preferences[TmdbAccountPreferenceKeys.GRAVATAR_HASH] = gravatarHash
                }
                ?: preferences.remove(TmdbAccountPreferenceKeys.GRAVATAR_HASH)
            preferences.remove(TmdbAccountPreferenceKeys.PENDING_REQUEST_TOKEN)
        }
    }

    suspend fun updateWatchlistSyncEnabled(enabled: Boolean) {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences[TmdbAccountPreferenceKeys.WATCHLIST_SYNC_ENABLED] = enabled
        }
    }

    suspend fun updateRatingsSyncEnabled(enabled: Boolean) {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences[TmdbAccountPreferenceKeys.RATINGS_SYNC_ENABLED] = enabled
        }
    }

    suspend fun clearAccount() {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences.remove(TmdbAccountPreferenceKeys.SESSION_ID)
            preferences.remove(TmdbAccountPreferenceKeys.PENDING_REQUEST_TOKEN)
            preferences.remove(TmdbAccountPreferenceKeys.ACCOUNT_ID)
            preferences.remove(TmdbAccountPreferenceKeys.USERNAME)
            preferences.remove(TmdbAccountPreferenceKeys.DISPLAY_NAME)
            preferences.remove(TmdbAccountPreferenceKeys.AVATAR_PATH)
            preferences.remove(TmdbAccountPreferenceKeys.GRAVATAR_HASH)
            preferences.remove(TmdbAccountPreferenceKeys.WATCHLIST_SYNC_ENABLED)
            preferences.remove(TmdbAccountPreferenceKeys.RATINGS_SYNC_ENABLED)
        }
    }
}
