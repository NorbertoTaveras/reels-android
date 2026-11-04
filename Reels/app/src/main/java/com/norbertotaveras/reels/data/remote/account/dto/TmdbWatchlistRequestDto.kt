/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.account.dto

import com.google.gson.annotations.SerializedName

data class TmdbWatchlistRequestDto(
    @SerializedName("media_type")
    val mediaType: String = "movie",
    @SerializedName("media_id")
    val mediaId: Int,
    @SerializedName("watchlist")
    val watchlist: Boolean,
)
