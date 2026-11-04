/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.account.model

data class TmdbAccountDataModel(
    val accountId: Int,
    val username: String,
    val displayName: String?,
    val avatarPath: String? = null,
    val gravatarHash: String? = null,
)
