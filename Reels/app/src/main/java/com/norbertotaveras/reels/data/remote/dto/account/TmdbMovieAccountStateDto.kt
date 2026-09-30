/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.account

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class TmdbMovieAccountStateDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("favorite")
    val favorite: Boolean,
    @SerializedName("watchlist")
    val watchlist: Boolean,
    @SerializedName("rated")
    val rated: JsonElement?,
)
