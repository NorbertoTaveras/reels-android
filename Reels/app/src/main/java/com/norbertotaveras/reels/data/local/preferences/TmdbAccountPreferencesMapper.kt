/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import androidx.datastore.preferences.core.Preferences
import com.norbertotaveras.reels.domain.model.account.TmdbAccountDataModel
import com.norbertotaveras.reels.domain.model.account.TmdbAccountStateDataModel

internal fun Preferences.toTmdbAccountState(): TmdbAccountStateDataModel {
    val accountId = this[TmdbAccountPreferenceKeys.ACCOUNT_ID]
    val username = this[TmdbAccountPreferenceKeys.USERNAME]

    return TmdbAccountStateDataModel(
        sessionId = this[TmdbAccountPreferenceKeys.SESSION_ID],
        pendingRequestToken = this[TmdbAccountPreferenceKeys.PENDING_REQUEST_TOKEN],
        isWatchlistSyncEnabled = this[TmdbAccountPreferenceKeys.WATCHLIST_SYNC_ENABLED] ?: true,
        isRatingsSyncEnabled = this[TmdbAccountPreferenceKeys.RATINGS_SYNC_ENABLED] ?: true,
        account = if (accountId != null && !username.isNullOrBlank()) {
            TmdbAccountDataModel(
                accountId = accountId,
                username = username,
                displayName = this[TmdbAccountPreferenceKeys.DISPLAY_NAME],
                avatarPath = this[TmdbAccountPreferenceKeys.AVATAR_PATH],
                gravatarHash = this[TmdbAccountPreferenceKeys.GRAVATAR_HASH]
            )
        } else {
            null
        }
    )
}
