/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
    val accountState: Flow<TmdbAccountStateDataModel> = context.tmdbAccountDataStore.data.map {
            preferences ->
        val accountId = preferences[ACCOUNT_ID_KEY]
        val username = preferences[USERNAME_KEY]

        TmdbAccountStateDataModel(
            sessionId = preferences[SESSION_ID_KEY],
            pendingRequestToken = preferences[PENDING_REQUEST_TOKEN_KEY],
            account = if (accountId != null && !username.isNullOrBlank()) {
                TmdbAccountDataModel(
                    accountId = accountId,
                    username = username,
                    displayName = preferences[DISPLAY_NAME_KEY]
                )
            } else {
                null
            }
        )
    }

    suspend fun savePendingRequestToken(requestToken: String) {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences[PENDING_REQUEST_TOKEN_KEY] = requestToken
        }
    }

    suspend fun saveConnectedAccount(
        sessionId: String,
        account: TmdbAccountDataModel,
    ) {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences[SESSION_ID_KEY] = sessionId
            preferences[ACCOUNT_ID_KEY] = account.accountId
            preferences[USERNAME_KEY] = account.username
            account.displayName
                ?.takeIf { displayName -> displayName.isNotBlank() }
                ?.let { displayName -> preferences[DISPLAY_NAME_KEY] = displayName }
                ?: preferences.remove(DISPLAY_NAME_KEY)
            preferences.remove(PENDING_REQUEST_TOKEN_KEY)
        }
    }

    suspend fun clearAccount() {
        context.tmdbAccountDataStore.edit { preferences ->
            preferences.remove(SESSION_ID_KEY)
            preferences.remove(PENDING_REQUEST_TOKEN_KEY)
            preferences.remove(ACCOUNT_ID_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(DISPLAY_NAME_KEY)
        }
    }

    private companion object {
        val SESSION_ID_KEY = stringPreferencesKey("session_id")
        val PENDING_REQUEST_TOKEN_KEY = stringPreferencesKey("pending_request_token")
        val ACCOUNT_ID_KEY = intPreferencesKey("account_id")
        val USERNAME_KEY = stringPreferencesKey("username")
        val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
    }
}
