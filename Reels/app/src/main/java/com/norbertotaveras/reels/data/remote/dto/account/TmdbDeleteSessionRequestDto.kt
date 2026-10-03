/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.dto.account

import com.google.gson.annotations.SerializedName

data class TmdbDeleteSessionRequestDto(
    @SerializedName("session_id")
    val sessionId: String,
)
