/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.account.model

object TmdbAuthenticationCallback {
    const val SCHEME = "reels"
    const val HOST = "tmdb-auth"
    const val PATH = "/approved"
    const val URI = "$SCHEME://$HOST$PATH"
}
