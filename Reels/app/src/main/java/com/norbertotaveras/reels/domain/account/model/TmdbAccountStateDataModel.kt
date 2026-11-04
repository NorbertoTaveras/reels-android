/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.account.model

data class TmdbAccountStateDataModel(
    val sessionId: String? = null,
    val pendingRequestToken: String? = null,
    val account: TmdbAccountDataModel? = null,
    val isWatchlistSyncEnabled: Boolean = true,
    val isRatingsSyncEnabled: Boolean = true,
) {
    val isConnected: Boolean
        get() = !sessionId.isNullOrBlank() && account != null

    val isApprovalPending: Boolean
        get() = !pendingRequestToken.isNullOrBlank() && !isConnected
}
